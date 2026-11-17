from fastapi import FastAPI
from pydantic import BaseModel, Field

app = FastAPI(title="NeuroHub API", version="0.1.0")

class ChatRequest(BaseModel):
    message: str = Field(min_length=1, max_length=20000)
    mode: str = "automatic"

class ChatResponse(BaseModel):
    reply: str
    selected_model: str
    status: str

@app.get("/health")
def health():
    return {"status": "ok", "service": "neurohub-api"}

@app.post("/v1/chat", response_model=ChatResponse)
def chat(request: ChatRequest):
    # Placeholder until secure provider adapters and the router are configured.
    return ChatResponse(
        reply="NeuroHub API is running. AI providers are not connected yet.",
        selected_model="none",
        status="scaffold"
    )
