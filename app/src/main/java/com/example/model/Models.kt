package com.example.model

/**
 * Connection states compatible with the target PcConnection StateFlow architecture.
 */
enum class PcConnectionStatus {
  CONNECTED,
  CONNECTING,
  DISCONNECTED,
  NOT_PAIRED
}

data class LocalModel(
  val id: String,
  val name: String,
  val isLoaded: Boolean = false,
  val description: String = ""
)

/**
 * Generic paired-computer presentation representing a paired desktop workstation
 * without fabricated GPU/hardware claims.
 */
data class PairedComputer(
  val id: String,
  val name: String,
  val statusDescription: String,
  val pcIdentityFingerprint: String,
  val isCurrent: Boolean = false,
  val lastSeen: String = "Active now"
)

data class CodeSnippet(
  val language: String,
  val code: String
)

data class ChatMessage(
  val id: String,
  val isUser: Boolean,
  val text: String,
  val timestamp: String,
  val codeSnippet: CodeSnippet? = null,
  val modelUsed: String? = null,
  val isStreaming: Boolean = false
)

data class Conversation(
  val id: String,
  val title: String,
  val previewText: String,
  val timestamp: String,
  val modelId: String,
  val modelName: String,
  val messages: List<ChatMessage>
)
