from typing import Tuple

from langchain_core.prompts import ChatPromptTemplate
from langchain_core.pydantic_v1 import BaseModel, Field
from langchain_openai import AzureChatOpenAI


class DecisionAgent:
    """
    Class to decide if a user prompt is a question or a runnable command.
    """

    class TextClassification(BaseModel):
        """
        Classifies the user prompt if it is a quesion or a runnable command
        """
        runnable: bool = Field(
            description=(
                "'True' if the text is a runnable or executable command, even if it is given in a question-like form."
                "'False' if the text is a regular question or a question about the possibility of something."
            )
        )

    prompt = ChatPromptTemplate.from_messages(
        [
            (
                "system",
                "You are a friendly assistant. Classify the user prompt if it is a quesion or a runnable command.",
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
    ).with_structured_output(TextClassification)

    chain = prompt | llm

    @classmethod
    async def decide(cls, text: str) -> bool:
        """Decide if the user prompt is runnable or not.

        Args:
            text (str): The piece of text to analyze.

        Returns:
            bool: Indicates wheter the user prompt wants to run something or it is just a question
        """

        if text.strip() == "":
            return False       
        
        try:
            res = await cls.chain.ainvoke({"user_prompt": text})
            return res.runnable

        except Exception as e:
            raise Exception(e) 