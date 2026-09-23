package com.example.core.community

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

class CommunityRepository {

  val xpLedger = ServerAuthoritativeXpLedger()

  private val _currentUser = MutableStateFlow(
    UserProfile(
      id = "user_me",
      username = "developer_iran",
      displayName = "برنامه‌نویس توانا",
      bio = "علاقه‌مند به توسعه کاتلین و وب‌اپلیکیشن‌های موبایل در بستر ABLE Forge.",
      skills = listOf("Kotlin", "Jetpack Compose", "JavaScript", "Git", "Accessibility"),
      xpBalance = 250L,
      referralCode = "ABLE-DEV-77",
      isVerified = true
    )
  )
  val currentUser: Flow<UserProfile> = _currentUser.asStateFlow()

  private val _chatMessages = MutableStateFlow<Map<String, List<ChatMessage>>>(
    mapOf(
      CommunityTopic.KOTLIN.id to listOf(
        ChatMessage("m1", CommunityTopic.KOTLIN.id, "u_reza", "رضا صادقی", "سلام به همه، نحوه مدیریت حافظه در روم خیلی عالی شده."),
        ChatMessage("m2", CommunityTopic.KOTLIN.id, "user_me", "برنامه‌نویس توانا", "کاملاً موافقم. مخصوصاً استفاده از کاتلین کوروتینز.")
      ),
      CommunityTopic.ACCESSIBILITY.id to listOf(
        ChatMessage("m3", CommunityTopic.ACCESSIBILITY.id, "u_sara", "سارا راد", "کنتراست بالا و رعایت تاچ تارگت ۴۸ دی‌پی برای همه کاربردی است.")
      ),
      CommunityTopic.JOINT_PROJECTS.id to listOf(
        ChatMessage("m4", CommunityTopic.JOINT_PROJECTS.id, "u_ali", "علی تهرانی", "برای پروژه وب‌اپ مشترک، به یک آزمون‌گر دسترسی‌پذیری نیازمندیم.")
      )
    )
  )
  val chatMessages: Flow<Map<String, List<ChatMessage>>> = _chatMessages.asStateFlow()
  val allChatMessages: Flow<List<ChatMessage>> = _chatMessages.map { it.values.flatten() }

  private val _showcases = MutableStateFlow(
    listOf(
      ProjectShowcase(
        id = "show_1",
        projectId = 1L,
        title = "AbleForge-Core",
        description = "کارگاه موبایلی کامل توسعه نرم‌افزار به صورت آفلاین-محور.",
        authorName = "برنامه‌نویس توانا",
        tags = listOf("Kotlin", "Android", "Git"),
        hasAbleFlag = true,
        neededRoles = listOf(CollaborationRole.TESTER, CollaborationRole.ACCESSIBILITY_TESTER),
        collaboratorsCount = 3
      ),
      ProjectShowcase(
        id = "show_2",
        projectId = 2L,
        title = "MathLogic-Kotlin",
        description = "موتور محاسبات الگوریتمی همراه با تست‌های محلی بدون اینترنت.",
        authorName = "تیم توانا",
        tags = listOf("Math", "Algorithms"),
        hasAbleFlag = false,
        neededRoles = listOf(CollaborationRole.TRANSLATOR),
        collaboratorsCount = 1
      )
    )
  )
  val showcases: Flow<List<ProjectShowcase>> = _showcases.asStateFlow()

  private val _blockedUsers = MutableStateFlow<Set<String>>(emptySet())
  val blockedUsers: Flow<Set<String>> = _blockedUsers.asStateFlow()

  private val _reports = MutableStateFlow<List<ModerationReport>>(emptyList())
  val reports: Flow<List<ModerationReport>> = _reports.asStateFlow()

  // Anti-spam tracker (message timestamps)
  private val recentMessageTimestamps = mutableListOf<Long>()

  fun addSkill(skill: String) {
    val clean = skill.trim()
    if (clean.isBlank()) return
    val current = _currentUser.value
    if (!current.skills.contains(clean)) {
      _currentUser.value = current.copy(skills = current.skills + clean)
    }
  }

  fun removeSkill(skill: String) {
    val current = _currentUser.value
    _currentUser.value = current.copy(skills = current.skills.filter { it != skill })
  }

  fun updateBio(newBio: String) {
    _currentUser.value = _currentUser.value.copy(bio = newBio.trim())
  }

  fun postMessage(topicId: String, content: String): Result<ChatMessage> {
    val clean = content.trim()
    if (clean.isEmpty()) {
      return Result.failure(IllegalArgumentException("Message content cannot be empty."))
    }

    // Spam Protection: Rate limit check (max 5 messages per 10 seconds)
    val now = System.currentTimeMillis()
    recentMessageTimestamps.removeAll { now - it > 10_000L }
    if (recentMessageTimestamps.size >= 5) {
      return Result.failure(IllegalStateException("Spam protection triggered: Please wait a moment before sending more messages."))
    }
    recentMessageTimestamps.add(now)

    val user = _currentUser.value
    val msg = ChatMessage(
      id = "msg_${System.currentTimeMillis()}",
      topicId = topicId,
      senderId = user.id,
      senderName = user.displayName,
      content = clean,
      timestamp = now
    )

    val currentMap = _chatMessages.value.toMutableMap()
    val topicList = currentMap[topicId]?.toMutableList() ?: mutableListOf()
    topicList.add(msg)
    currentMap[topicId] = topicList
    _chatMessages.value = currentMap

    return Result.success(msg)
  }

  fun blockUser(userId: String) {
    val newSet = _blockedUsers.value + userId
    _blockedUsers.value = newSet
    _currentUser.value = _currentUser.value.copy(blockedUserIds = newSet)
  }

  fun unblockUser(userId: String) {
    val newSet = _blockedUsers.value - userId
    _blockedUsers.value = newSet
    _currentUser.value = _currentUser.value.copy(blockedUserIds = newSet)
  }

  fun reportContent(targetId: String, reason: String) {
    val report = ModerationReport(
      reportId = "rep_${System.currentTimeMillis()}",
      reporterId = _currentUser.value.id,
      targetEntityId = targetId,
      reason = reason
    )
    _reports.value = _reports.value + report
  }

  fun reportMessage(messageId: String, reason: String) {
    reportContent(messageId, reason)
  }

  fun sendOrdinaryGift(receiverId: String, amount: Long): Result<XpEvent> {
    return try {
      val giver = _currentUser.value
      val event = xpLedger.executeOrdinaryGift(
        giverId = giver.id,
        receiverId = receiverId,
        amount = amount,
        giverBalance = giver.xpBalance
      )
      _currentUser.value = giver.copy(xpBalance = giver.xpBalance - amount)
      Result.success(event)
    } catch (e: Exception) {
      Result.failure(e)
    }
  }
}
