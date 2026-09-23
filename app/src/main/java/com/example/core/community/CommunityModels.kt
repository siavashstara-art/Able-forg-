package com.example.core.community

/**
 * Feature Flags for Community and Economy modules.
 * Strictly guarantees modular decoupling without affecting Core Development.
 */
object CommunityFeatureFlags {
  const val COMMUNITY_ENABLED = true
  const val ECONOMY_ENABLED = true
  const val SPAM_PROTECTION_ENABLED = true

  // Explicitly DISABLED in v0.1 as per specification
  const val GROUP_SYSTEM_ENABLED = false
  const val RECOGNITION_TITLES_ENABLED = false
}

enum class CollaborationRole(val titleFa: String, val titleEn: String) {
  DEVELOPER("توسعه‌دهنده نرم‌افزار", "Software Developer"),
  DESIGNER("طراح رابط و تجربه کاربری", "UI/UX Designer"),
  TRANSLATOR("مترجم و بومی‌ساز", "Translator & Localizer"),
  TESTER("آزمون‌گر نرم‌افزار", "Software QA Tester"),
  ACCESSIBILITY_TESTER("آزمون‌گر دسترسی‌پذیری و فراگیر", "Accessibility QA Specialist")
}

data class UserProfile(
  val id: String,
  val username: String,
  val displayName: String,
  val bio: String,
  val skills: List<String>,
  val xpBalance: Long = 100L,
  val referralCode: String,
  val isVerified: Boolean = false,
  val blockedUserIds: Set<String> = emptySet(),
  val privacyAllowMessages: Boolean = true
)

enum class CommunityTopic(val id: String, val titleFa: String, val titleEn: String, val iconLabel: String) {
  ANDROID("android", "اندروید", "Android", "🤖"),
  WEB("web", "وب و جاوااسکریپت", "Web", "🌐"),
  GITHUB("github", "گیت‌هاب و کنترل نسخه", "GitHub", "🐙"),
  AI("ai", "هوش مصنوعی و یادگیری ماشین", "AI", "✨"),
  JAVASCRIPT("javascript", "جاوااسکریپت", "JavaScript", "⚡"),
  KOTLIN("kotlin", "کاتلین و کامپوز", "Kotlin", "🟣"),
  PYTHON("python", "پایتون", "Python", "🐍"),
  DEBUGGING("debugging", "رفع اشکال و تست", "Debugging", "🔍"),
  ACCESSIBILITY("accessibility", "دسترسی‌پذیری و فراگیری", "Accessibility", "♿"),
  JOINT_PROJECTS("joint_projects", "پروژه‌های مشترک", "Joint Projects", "🤝"),
  BEGINNERS("beginners", "نوآموزان و شروع کار", "Beginners", "🌱")
}

data class ChatMessage(
  val id: String,
  val topicId: String,
  val senderId: String,
  val senderName: String,
  val content: String,
  val timestamp: Long = System.currentTimeMillis(),
  val isReported: Boolean = false
)

data class ProjectShowcase(
  val id: String,
  val projectId: Long,
  val title: String,
  val description: String,
  val authorName: String,
  val tags: List<String>,
  val hasAbleFlag: Boolean,
  val neededRoles: List<CollaborationRole> = emptyList(),
  val collaboratorsCount: Int = 1
)

data class CollaborationOpportunity(
  val id: String,
  val projectId: Long,
  val projectTitle: String,
  val role: CollaborationRole,
  val description: String,
  val applicantCount: Int = 0
)

data class ModerationReport(
  val reportId: String,
  val reporterId: String,
  val targetEntityId: String, // MessageId or UserId
  val reason: String,
  val timestamp: Long = System.currentTimeMillis()
)

// --- Architecture for Future Modules (Strictly DISABLED in v0.1) ---

/**
 * Uniform Role Vocabulary for Group System.
 * Group system is currently DISABLED.
 */
enum class StandardGroupRole(val id: String, val title: String) {
  MEMBER("member", "Member"),
  CONTRIBUTOR("contributor", "Contributor"),
  COORDINATOR("coordinator", "Coordinator"),
  MAINTAINER("maintainer", "Maintainer")
}

/**
 * Future Recognition Data Contract (Titles, Hall of Fame, Legacy).
 * Recognition system is currently DISABLED in v0.1.
 */
data class FutureRecognitionContract(
  val userId: String,
  val title: String,
  val category: String, // "Titles" | "HallOfFame" | "Legacy"
  val isEnabled: Boolean = CommunityFeatureFlags.RECOGNITION_TITLES_ENABLED
)
