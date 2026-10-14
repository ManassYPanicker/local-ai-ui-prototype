package com.example.data

import com.example.model.ChatMessage
import com.example.model.CodeSnippet
import com.example.model.Conversation
import com.example.model.LocalModel
import com.example.model.PairedComputer

object DemoDataProvider {

  /**
   * Prototype preview models.
   * In production, model lists are retrieved dynamically from the paired PC via LM Studio's /v1/models endpoint.
   */
  val sampleModels = listOf(
    LocalModel(
      id = "qwen2.5-coder-32b-instruct",
      name = "Qwen 2.5 Coder 32B",
      isLoaded = true,
      description = "Code generation and engineering reasoning"
    ),
    LocalModel(
      id = "deepseek-r1-distill-qwen-14b",
      name = "DeepSeek R1 Distill 14B",
      isLoaded = false,
      description = "Step-by-step analytical reasoning and math"
    ),
    LocalModel(
      id = "llama-3.3-70b-instruct",
      name = "Llama 3.3 70B",
      isLoaded = false,
      description = "General-purpose long-form synthesis"
    ),
    LocalModel(
      id = "mistral-nemo-instruct-2407",
      name = "Mistral Nemo 12B",
      isLoaded = false,
      description = "Fast context-window processing"
    )
  )

  /**
   * Generic paired-computer presentation without fabricated hardware telemetry.
   */
  val sampleComputers = listOf(
    PairedComputer(
      id = "workstation-primary",
      name = "Studio Desktop",
      statusDescription = "Ready for requests",
      pcIdentityFingerprint = "7F:4A:91:E2:88:51:30:2C:94:11:0B:DA:C3:5E:21:88",
      isCurrent = true,
      lastSeen = "Connected"
    ),
    PairedComputer(
      id = "workstation-secondary",
      name = "Home Office PC",
      statusDescription = "Connector not reachable",
      pcIdentityFingerprint = "B2:44:81:99:A1:76:30:FE:19:55:7C:20:EA:61:94:02",
      isCurrent = false,
      lastSeen = "2 hours ago"
    )
  )

  fun getInitialConversations(): List<Conversation> {
    return listOf(
      Conversation(
        id = "conv-1",
        title = "PyTorch Triton FlashAttention Kernel",
        previewText = "Here is the Triton-based causal masked attention kernel using block tiling and shared SRAM...",
        timestamp = "12m ago",
        modelId = "qwen2.5-coder-32b-instruct",
        modelName = "Qwen 2.5 Coder 32B",
        messages = listOf(
          ChatMessage(
            id = "msg-1-1",
            isUser = true,
            text = "Can you write an optimized Triton causal attention kernel for PyTorch with block-level tiling?",
            timestamp = "10:14 AM"
          ),
          ChatMessage(
            id = "msg-1-2",
            isUser = false,
            text = "Here is an optimized Triton causal attention forward kernel. It computes softmax over queries and keys inside SRAM tiles (BLOCK_M x BLOCK_N) to avoid materializing the full N×N attention matrix in main memory:",
            timestamp = "10:15 AM",
            codeSnippet = CodeSnippet(
              language = "python",
              code = """@triton.jit
def _fwd_kernel(
    Q, K, V, sm_scale,
    L, Out,
    stride_qz, stride_qh, stride_qm, stride_qk,
    stride_kz, stride_kh, stride_kn, stride_kk,
    Z, H, N_CTX,
    BLOCK_M: tl.constexpr, BLOCK_DMODEL: tl.constexpr,
    BLOCK_N: tl.constexpr,
):
    start_m = tl.program_id(0)
    offs_m = start_m * BLOCK_M + tl.arange(0, BLOCK_M)
    offs_n = tl.arange(0, BLOCK_N)
    offs_d = tl.arange(0, BLOCK_DMODEL)
    
    # Load Q block with SRAM registers
    q_ptrs = Q + offs_m[:, None] * stride_qm + offs_d[None, :] * stride_qk
    q = tl.load(q_ptrs, mask=offs_m[:, None] < N_CTX, other=0.0)
    
    m_i = tl.zeros([BLOCK_M], dtype=tl.float32) - float("inf")
    l_i = tl.zeros([BLOCK_M], dtype=tl.float32)
    acc = tl.zeros([BLOCK_M, BLOCK_DMODEL], dtype=tl.float32)
    
    # Causal loop over key/value tiles
    for start_n in range(0, (start_m + 1) * BLOCK_M, BLOCK_N):
        k_ptrs = K + (start_n + offs_n[None, :]) * stride_kn + offs_d[:, None] * stride_kk
        k = tl.load(k_ptrs)
        qk = tl.dot(q, k) * sm_scale
        # Apply causal mask
        qk = tl.where(offs_m[:, None] >= (start_n + offs_n[None, :]), qk, -float("inf"))
        
        m_curr = tl.maximum(m_i, tl.max(qk, 1))
        p = tl.exp(qk - m_curr[:, None])
        l_curr = tl.exp(m_i - m_curr) * l_i + tl.sum(p, 1)
        
        v_ptrs = V + (start_n + offs_n[:, None]) * stride_kn + offs_d[None, :] * stride_kk
        v = tl.load(v_ptrs)
        acc = acc * tl.exp(m_i - m_curr)[:, None] + tl.dot(p.to(tl.float16), v)
        m_i = m_curr
        l_i = l_curr
        
    acc = acc / l_i[:, None]
    tl.store(Out + offs_m[:, None] * stride_qm + offs_d[None, :], acc.to(tl.float16))"""
            ),
            modelUsed = "Qwen 2.5 Coder 32B"
          )
        )
      ),
      Conversation(
        id = "conv-2",
        title = "Local Offline RAG Architecture",
        previewText = "For 15,000 markdown notes, LM Studio's local embeddings endpoint with nomic-embed-text...",
        timestamp = "2h ago",
        modelId = "deepseek-r1-distill-qwen-14b",
        modelName = "DeepSeek R1 Distill 14B",
        messages = listOf(
          ChatMessage(
            id = "msg-2-1",
            isUser = true,
            text = "How should I structure a local RAG pipeline for 15,000 markdown research notes using LM Studio?",
            timestamp = "8:30 AM"
          ),
          ChatMessage(
            id = "msg-2-2",
            isUser = false,
            text = "To structure an efficient local RAG pipeline connecting through your paired PC:\n\n1. Embedding Generation: Load an embedding model in LM Studio for generating 768-dim embeddings.\n2. Vector Store: Use a local vector index (such as sqlite-vec or Chroma) stored on disk.\n3. Chunking: Recursive character splitting with 512 tokens and 64-token overlap, retaining file headers as document metadata.\n4. Reranking: Optional lightweight cross-encoder to select the top candidate chunks for context injection.\n\nAll queries route through the PC connector directly to your computer's local LM Studio server.",
            timestamp = "8:32 AM",
            modelUsed = "DeepSeek R1 Distill 14B"
          )
        )
      ),
      Conversation(
        id = "conv-3",
        title = "Quantization: GGUF vs EXL2 Concepts",
        previewText = "Comparing GGUF and EXL2 formats when executing open-weights models locally...",
        timestamp = "Yesterday",
        modelId = "llama-3.3-70b-instruct",
        modelName = "Llama 3.3 70B",
        messages = listOf(
          ChatMessage(
            id = "msg-3-1",
            isUser = true,
            text = "What is the practical difference between llama.cpp GGUF and EXL2 format for local inference?",
            timestamp = "Yesterday"
          ),
          ChatMessage(
            id = "msg-3-2",
            isUser = false,
            text = "Key architectural differences between the formats:\n\n• GGUF (llama.cpp): Versatile multi-platform format supporting hybrid CPU/GPU offloading, mmap loading, and widespread hardware support. Ideal for running models across varying hardware capacities.\n• EXL2 (ExLlamaV2): Tailored specifically for NVIDIA GPUs with variable-bit quantization per layer and optimized custom CUDA kernels.\n\nLM Studio handles runtime execution on your PC, and this app communicates with it through your secure connector.",
            timestamp = "Yesterday",
            modelUsed = "Llama 3.3 70B"
          )
        )
      )
    )
  }

  val sampleStreamingPromptResponses = mapOf(
    "How does LM Studio serve local models over the LAN?" to """LM Studio runs an OpenAI-compatible HTTP server on your computer (typically at localhost:1234).

In our application architecture:
1. PC Connector: A lightweight companion connector service on your PC manages authenticated pairing with your phone.
2. Secure Transport: The Android app connects to the PC connector via an encrypted transport tunnel.
3. Local Forwarding: The connector communicates with LM Studio over localhost, allowing your phone to query your personal models from anywhere without third-party cloud inference.""",

    "Show me a quick Python script to query the local server" to """import requests

# Direct localhost call on the PC running LM Studio:
LM_STUDIO_URL = "http://localhost:1234/v1/chat/completions"

payload = {
    "model": "qwen2.5-coder-32b-instruct",
    "messages": [
        {"role": "system", "content": "You are a concise engineering assistant."},
        {"role": "user", "content": "Explain KV-cache compression in 2 sentences."}
    ],
    "temperature": 0.2,
    "stream": True
}

response = requests.post(LM_STUDIO_URL, json=payload, stream=True)
for line in response.iter_lines():
    if line:
        print(line.decode('utf-8'))"""
  )
}
