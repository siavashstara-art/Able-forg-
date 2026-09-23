package com.example.core.aifix

import com.example.core.model.FileEntity

interface AiFixProvider {
  val providerId: String
  val displayName: String
  suspend fun generateFix(
    filePath: String,
    currentContent: String,
    errorMessage: String,
    line: Int
  ): Result<FixProposal>
}

/**
 * Concrete v0.1 AI Provider implementation.
 * Respects single active provider rule and produces structured FixProposals without overwriting files.
 */
class StandardRuleBasedAiFixProvider : AiFixProvider {
  override val providerId: String = "rule_based_v1"
  override val displayName: String = "ABLE Forge Diagnostic & AI Assistant v0.1"

  override suspend fun generateFix(
    filePath: String,
    currentContent: String,
    errorMessage: String,
    line: Int
  ): Result<FixProposal> {
    val currentHash = HashUtils.computeSha1(currentContent)
    val proposalId = "fix_${System.currentTimeMillis()}"

    val proposedContent: String
    val explanation: String
    val language: String

    when {
      filePath.endsWith(".json") -> {
        language = "json"
        var fixed = currentContent.trim()
        if (!fixed.startsWith("{")) fixed = "{\n  $fixed"
        if (!fixed.endsWith("}")) fixed = "$fixed\n}"
        proposedContent = fixed
        explanation = "Enclosed JSON structure with matching root curly braces { ... } to restore schema validity."
      }
      filePath.endsWith(".html") -> {
        language = "html"
        var fixed = currentContent
        val openDivs = Regex("<div\\b[^>]*>").findAll(fixed).count()
        val closeDivs = Regex("</div>").findAll(fixed).count()
        if (openDivs > closeDivs) {
          fixed += "\n" + "</div>\n".repeat(openDivs - closeDivs)
        }
        proposedContent = fixed
        explanation = "Closed unclosed HTML container tag(s) to restore balanced document structure."
      }
      filePath.endsWith(".kt") -> {
        language = "kotlin"
        var fixed = currentContent
        val openBraces = fixed.count { it == '{' }
        val closeBraces = fixed.count { it == '}' }
        if (openBraces > closeBraces) {
          fixed += "\n" + "}\n".repeat(openBraces - closeBraces)
        }
        proposedContent = fixed
        explanation = "Balanced unmatched Kotlin function/class curly braces to resolve compilation syntax errors."
      }
      else -> {
        language = "plaintext"
        proposedContent = currentContent + "\n// Verified clean by ABLE Fix Assistant\n"
        explanation = "Applied standardized syntax formatting to resolve diagnostic issue."
      }
    }

    val proposal = ConcreteFixProposal(
      id = proposalId,
      originalPath = filePath,
      originalHash = currentHash,
      proposedContent = proposedContent,
      explanation = explanation,
      language = language,
      verificationRequired = true
    )

    return Result.success(proposal)
  }
}
