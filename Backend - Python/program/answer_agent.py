from langchain_core.prompts import ChatPromptTemplate
from langchain_core.pydantic_v1 import BaseModel, Field
from langchain_openai import AzureChatOpenAI


class AnswerAgent:
    """
    Class to answer a user prompt.
    """
    
    prompt = ChatPromptTemplate.from_messages(
        [
            (
                "system",
                "You are a friendly voice assistant. Answer the question on the source language with less than 3 sentences",
            ),
            ("human", "{user_prompt}"),
        ]
    )

    llm = AzureChatOpenAI(
        azure_deployment="gpt-4o", #or mini or normal
        api_version="2024-06-01",
        temperature=0,
        max_tokens=None,
        timeout=None,
        max_retries=2,
    )

    chain = prompt | llm

    @classmethod
    async def answer(cls, text: str) -> str:
        """Answer the user prompt.

        Args:
            text (str): The piece of text to answer.

        Returns:
            str: The answer for the question
        """

        if text.strip() == "":
            return False       
        
        try:
            res = await cls.chain.ainvoke({"user_prompt": text})
            return res.content

        except Exception as e:
            raise Exception(e) 