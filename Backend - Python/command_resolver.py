import os
from typing import Tuple

from langchain_core.prompts import ChatPromptTemplate
from langchain_core.pydantic_v1 import BaseModel, Field
from langchain_openai import AzureChatOpenAI
import pandas as pd
from langchain_huggingface import HuggingFaceEmbeddings
from langchain_core.documents import Document
from langchain_community.vectorstores import FAISS


class CommandResolver:
    """
    Class to choose the appropriate runnable command.
    """

    prompt = ChatPromptTemplate.from_messages(
        [
            (
                "system",
                ("You are a helpful assistant that decides which function should be choosed based on the user prompt."
                "If no function is close to the user prompt, then simply do not select a function")
            ),
            ("human", "{user_prompt}"),
        ]
    )

    df = pd.read_csv('misc/commands.csv', header=0, sep=";")

    embeddings = HuggingFaceEmbeddings(model_name="paraphrase-multilingual-MiniLM-L12-v2")

    docs = []

   # Define the file path where you want to store the FAISS index
    faiss_index_path = "faiss_db"

    if os.path.exists(faiss_index_path):
        print("FAISS index found. Loading the index from file.")
        # Load the FAISS index from the file
        db = FAISS.load_local(faiss_index_path, embeddings, embeddings,allow_dangerous_deserialization=True)
    else:
        print("FAISS index not found. Creating a new index.")
        docs = []
        for index, row in df.iterrows():
            doc = Document(
                page_content=row['Description'],
                metadata={
                    "source": row['Name'], 
                    "param1": row['Param1'], "param1type": row['Param1Type'], "param1required": row['Param1Required'], "param1description": row['Param1Description'],
                    "param2": row['Param2'], "param2type": row['Param2Type'], "param2required": row['Param2Required'], "param2description": row['Param2Description']
                },
            )
            docs.append(doc)

        db = FAISS.from_documents(docs, embeddings)
        db.save_local(faiss_index_path)
        print(f"FAISS index saved to {faiss_index_path}.")

    llm = AzureChatOpenAI(
        azure_deployment="gpt-4o",
        api_version="2024-06-01",
        temperature=0,
        max_tokens=None,
        timeout=None,
        max_retries=2,
    )

    @classmethod
    async def select(cls, text: str) -> Tuple[dict,str]:
        """Selects the correct function based on the user prompt.

        Args:
            text (str): The user prompt.

        Returns:
            Tuple[dict,str]: The selected function and an error answer
        """

        if text.strip() == "":
            return False, ""     

        functions = cls.db.similarity_search(
            text,
            k=5,
        )
    
        tool_list = []
        
        # according to https://blog.langchain.dev/tool-calling-with-langchain/
        
        for function in functions:
        
            # isinstance to check type, as nan is not a string
        
            # has 2 params
            if isinstance(function.metadata["param2"], str):
                required = []
                if (function.metadata["param1required"] == 1.0):
                    required.append(function.metadata["param1"])
        
                if (function.metadata["param2required"] == 1.0):
                    required.append(function.metadata["param2"])
                
                tool = {
                    "name": function.metadata['source'],
                    "description": function.page_content,
                    "parameters" : {
                        "type": "object",
                        "properties": {
                            function.metadata["param1"]: {"type": function.metadata["param1type"], "description": function.metadata["param1description"]},
                            function.metadata["param2"]: {"type": function.metadata["param2type"], "description": function.metadata["param2description"]},
                        },
                    },
                    "required": required
                }
                tool_list.append(tool)
        
            # has 1 param
            elif isinstance(function.metadata["param1"], str):
                required = []
                if (function.metadata["param1required"] == 1.0):
                    required.append(function.metadata["param1"])
                
                tool = {
                    "name": function.metadata['source'],
                    "description": function.page_content,
                    "parameters" : {
                        "type": "object",
                        "properties": {
                          function.metadata["param1"]: {"type": function.metadata["param1type"], "description": function.metadata["param1description"]}
                        }
                    },
                    "required": required
                }
                tool_list.append(tool)
        
            #no param
            else:
                tool = {
                    "name": function.metadata['source'],
                    "description": function.page_content,
                    "parameters" : {},
                    "required": []
                }
                tool_list.append(tool)
        
        llm_with_funcs = cls.llm.bind_tools(tool_list)

        chain = cls.prompt | llm_with_funcs
   
        try:
            res = await chain.ainvoke({"user_prompt": text})        
            if hasattr(res, 'tool_calls') and len(res.tool_calls) > 0:
                
                called_function_name = res.tool_calls[0]['name']
                called_function_desc = [function for function in tool_list if function['name'] == called_function_name][0]['description']

                prompt = ChatPromptTemplate.from_messages(
                    [
                        (
                            "system",
                            ("You are a friendly assistant, and you should give a polite short answer to the user prompt as a feedback that it will do an action."
                            f"The called function is {called_function_name}, and its description is {called_function_desc}.")
                        ),
                        ("human", "{user_prompt}"),
                    ]
                )
                chain_react = prompt | cls.llm
                answer = await chain_react.ainvoke({"user_prompt": text})

                return res.tool_calls[0], answer.content
                
            else:
                return {}, res.content

        except Exception as e:
            raise Exception(e) 