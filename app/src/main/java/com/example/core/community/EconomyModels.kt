package com.example.core.community

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class XpEventType {
  ORDINARY_GIFT,
  COLLABORATION_REWARD,
  VALUE_CREATION_PURCHASE,
  REFERRAL_SIGNUP,
  PROJECT_DEPLOYMENT
}

/**
 * Server-authoritative and Ledger-based XP Event model.
 */
data class XpEvent(
  val id: String,
  val source: String,
  val receiver: String,
  val amount: Long,
  val type: XpEventType,
  val projectId: Long? = null,
  val timestamp: Long = System.currentTimeMillis(),
  val ruleVersion: String = "v1.0"
)

/**
 * ABLE Flag definition:
 * - Indicator of ABLE ecosystem participation.
 * - DOES NOT transfer economic or IP ownership (Creator strictly retains all IP rights).
 * - Requires standard ABLE logo/banner on deployed project.
 * - Enables higher Collaboration XP tiers.
 */
data class AbleFlag(
  val projectId: Long,
  val isFlagged: Boolean = false,
  val bannerText: String = "Crafted with ABLE Forge / کارگاه توانا",
  val standardLogoUrl: String = "res/drawable/ic_launcher_foreground.png",
  val ipOwnershipDisclaimer: String = "The creator/author strictly retains all economic and intellectual property ownership. ABLE participation does not transfer IP rights."
)

class XpRuleViolationException(message: String) : Exception(message)

/**
 * Server-authoritative ledger engine enforcing ABLE Forge economic rules.
 */
class ServerAuthoritativeXpLedger {

  private val events = mutableListOf<XpEvent>()
  private val giftHistory = mutableListOf<XpEvent>()
  private val _history = MutableStateFlow<List<XpEvent>>(
    listOf(
      XpEvent(
        id = "init_xp_1",
        source = "SYSTEM_WELCOME",
        receiver = "user_me",
        amount = 100L,
        type = XpEventType.REFERRAL_SIGNUP,
        timestamp = System.currentTimeMillis() - 86400000L
      )
    )
  )
  val history: StateFlow<List<XpEvent>> = _history.asStateFlow()

  companion object {
    const val RULE_VERSION = "v1.0"
    const val ORDINARY_GIFT_MAX_PERCENTAGE = 0.30 // 30% cap
    const val ONE_MONTH_MILLIS = 30L * 24 * 60 * 60 * 1000L
    const val SIX_MONTHS_MILLIS = 180L * 24 * 60 * 60 * 1000L

    // Explicit Non-Guarantee Legal Clause
    const val NON_GUARANTEE_DISCLAIMER =
      "XP points or purchases DO NOT guarantee that an individual receives land, employment, a management position, or a council seat."
  }

  fun getEvents(): List<XpEvent> = events.toList()

  /**
   * Ordinary XP Gift Rules:
   * 1. Giver can gift at most once per month.
   * 2. Cannot gift to the same user/ID more than once in 6 months (anti-farming).
   * 3. Max gift cap: 30% of giver's available balance.
   * 4. Does NOT require ABLE Flag.
   */
  fun executeOrdinaryGift(
    giverId: String,
    receiverId: String,
    amount: Long,
    giverBalance: Long,
    currentTime: Long = System.currentTimeMillis()
  ): XpEvent {
    if (giverId == receiverId) {
      throw XpRuleViolationException("Cannot gift XP to yourself.")
    }

    if (amount <= 0) {
      throw XpRuleViolationException("Gift amount must be positive.")
    }

    val maxAllowedGift = (giverBalance * ORDINARY_GIFT_MAX_PERCENTAGE).toLong()
    if (amount > maxAllowedGift) {
      throw XpRuleViolationException("Ordinary XP gift exceeds 30% cap. Max allowed: $maxAllowedGift XP.")
    }

    // Check once per month condition for giver
    val lastGiftByGiver = giftHistory.findLast { it.source == giverId && it.type == XpEventType.ORDINARY_GIFT }
    if (lastGiftByGiver != null && (currentTime - lastGiftByGiver.timestamp) < ONE_MONTH_MILLIS) {
      val daysLeft = ((ONE_MONTH_MILLIS - (currentTime - lastGiftByGiver.timestamp)) / (24 * 60 * 60 * 1000L)) + 1
      throw XpRuleViolationException("Ordinary XP gifting is limited to once a month. Please wait $daysLeft more day(s).")
    }

    // Check once in 6 months to the same receiver (anti-farming)
    val lastGiftToReceiver = giftHistory.findLast {
      it.source == giverId && it.receiver == receiverId && it.type == XpEventType.ORDINARY_GIFT
    }
    if (lastGiftToReceiver != null && (currentTime - lastGiftToReceiver.timestamp) < SIX_MONTHS_MILLIS) {
      throw XpRuleViolationException("Anti-farming rule: You cannot gift XP to the same recipient more than once in 6 months.")
    }

    val event = XpEvent(
      id = "xp_${System.currentTimeMillis()}_${events.size}",
      source = giverId,
      receiver = receiverId,
      amount = amount,
      type = XpEventType.ORDINARY_GIFT,
      projectId = null,
      timestamp = currentTime,
      ruleVersion = RULE_VERSION
    )

    events.add(event)
    giftHistory.add(event)
    _history.value = _history.value + event
    return event
  }

  /**
   * Collaboration XP Rules:
   * 1. Project MUST be under ABLE Flag.
   * 2. Real collaboration verification required (collaborators count >= 2, verified contributions).
   * 3. Caps:
   *    - 2 collaborators: 60%
   *    - 3 collaborators: 70%
   *    - 4+ collaborators: 80%
   */
  fun calculateCollaborationXpCap(collaboratorsCount: Int, hasAbleFlag: Boolean): Double {
    if (!hasAbleFlag) {
      return 0.0 // Higher collaboration XP strictly requires ABLE Flag
    }
    return when (collaboratorsCount) {
      0, 1 -> 0.0
      2 -> 0.60
      3 -> 0.70
      else -> 0.80
    }
  }

  fun awardCollaborationXp(
    projectId: Long,
    receiverId: String,
    collaboratorsCount: Int,
    hasAbleFlag: Boolean,
    basePoolAmount: Long,
    isRealVerifiedCollaboration: Boolean
  ): XpEvent {
    if (!hasAbleFlag) {
      throw XpRuleViolationException("Collaboration XP above standard cap is strictly reserved for projects deployed under the ABLE Flag.")
    }

    if (!isRealVerifiedCollaboration || collaboratorsCount < 2) {
      throw XpRuleViolationException("Real collaboration verification required. Simply creating a nominal project or adding names is insufficient.")
    }

    val capPercentage = calculateCollaborationXpCap(collaboratorsCount, hasAbleFlag)
    val maxAllowedReward = (basePoolAmount * capPercentage).toLong()

    val event = XpEvent(
      id = "collab_${System.currentTimeMillis()}_$projectId",
      source = "SYSTEM_ABLE_COLLABORATION",
      receiver = receiverId,
      amount = maxAllowedReward,
      type = XpEventType.COLLABORATION_REWARD,
      projectId = projectId,
      timestamp = System.currentTimeMillis(),
      ruleVersion = RULE_VERSION
    )

    events.add(event)
    _history.value = _history.value + event
    return event
  }
}

/**
 * League of Value Creators:
 * Records eligible economic and qualifying purchasing activity.
 */
data class ValueCreationRecord(
  val recordId: String,
  val userId: String,
  val activityType: String,
  val qualifyingAmountUsd: Double,
  val xpAwarded: Long,
  val timestamp: Long = System.currentTimeMillis()
)

/**
 * League of Introducers:
 * Tracks referral code attribution, signups, and qualifying purchases.
 */
data class IntroducerReferralRecord(
  val referralId: String,
  val referrerCode: String,
  val referrerUserId: String,
  val invitedUserId: String,
  val qualifyingEvent: String,
  val xpAwarded: Long,
  val timestamp: Long = System.currentTimeMillis()
)
