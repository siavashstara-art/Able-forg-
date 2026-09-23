package com.example.core.guidance

/**
 * The 4 assistance tiers for ABLE Forge.
 * A. Self (خودم انجام می‌دهم)
 * B. Guidance (راهنمایی)
 * C. Teaching + Guidance (آموزش + راهنمایی)
 * D. Teaching + Guidance + Assistant (آموزش + راهنمایی + دستیار فعال)
 */
enum class GuidanceTier(
  val id: String,
  val faTitle: String,
  val enTitle: String,
  val faDesc: String,
  val enDesc: String
) {
  SELF(
    "self",
    "خودم انجام می‌دهم",
    "Self (Independent)",
    "هیچ پنجره یا راهنمای خودکاری باز نمی‌شود مگر اینکه خودت روی دکمه کمک بزنی.",
    "No automatic hints or popups unless you tap the Help button."
  ),
  GUIDANCE(
    "guidance",
    "راهنمایی مرحله‌ای",
    "Guidance",
    "نشانه‌ها و پیام‌های ساده و کوتاه در هر مرحله بهت یادآوری می‌کنند چه کاری انجام دهی.",
    "Gentle hints and traffic signs remind you what to do next."
  ),
  TEACHING_GUIDANCE(
    "teaching_guidance",
    "آموزش + راهنمایی",
    "Teaching + Guidance",
    "علاوه بر راهنمایی، مفهوم و دلیل هر کار را با زبانی کاملاً روشن و گام‌به‌گام توضیح می‌دهد.",
    "Explains reasons and concepts step-by-step in clear, plain language."
  ),
  TEACHING_GUIDANCE_ASSISTANT(
    "teaching_guidance_assistant",
    "آموزش + راهنمایی + دستیار",
    "Teaching + Guidance + Assistant",
    "بیشترین کمک ممکن! دکمه‌های آماده، پیشنهاد اقدامات خودکار، خواندن متن و عیب‌یابی آنی.",
    "Maximum support! Direct action buttons, auto-suggestions, voice readouts, and immediate fixes."
  )
}

/**
 * Whether the user chooses their own tier or lets the adaptive engine manage it.
 */
enum class GuidanceSelectionMode {
  UNSET,
  MANUAL,  // کاربر خودش انتخاب کرده
  ADAPTIVE // ABLE Forge به شکل خودکار بر اساس عملکرد تنظیم می‌کند
}

/**
 * Metric tracking for adaptive intelligence.
 */
data class AdaptiveUserMetrics(
  val successCount: Int = 0,
  val actionRepeats: Map<String, Int> = emptyMap(),
  val helpRequestsCount: Int = 0,
  val recentErrorsCount: Int = 0,
  val stepAbandonsCount: Int = 0,
  val continuedAfterHintCount: Int = 0,
  val requestMoreTeachingCount: Int = 0,
  val requestLessTeachingCount: Int = 0
)

/**
 * Guidance state representation.
 */
data class GuidanceState(
  val isFirstLaunchPromptActive: Boolean = false,
  val selectionMode: GuidanceSelectionMode = GuidanceSelectionMode.UNSET,
  val currentTier: GuidanceTier = GuidanceTier.TEACHING_GUIDANCE_ASSISTANT,
  val doNotDisturb: Boolean = false, // «فعلاً مزاحم نشو»
  val metrics: AdaptiveUserMetrics = AdaptiveUserMetrics(),
  val masterySuggestionVisible: Boolean = false,
  val currentTutorialChapterId: String = "start_from_zero"
)
