package com.example.core.monetization

enum class PlanTier {
  FREE,
  PREMIUM,
  PLUS
}

enum class PlanStatus {
  ACTIVE,
  EXPIRED,
  TRIAL,
  INACTIVE
}

data class SubscriptionDurationOption(
  val months: Int,
  val labelEn: String,
  val labelFa: String,
  val discountPercent: Int,
  val priceMultiplier: Double
)

data class SubscriptionPlan(
  val id: String,
  val tier: PlanTier,
  val nameEn: String,
  val nameFa: String,
  val featuresEn: List<String>,
  val featuresFa: List<String>,
  val limitsEn: Map<String, String>,
  val limitsFa: Map<String, String>,
  val basePriceTomanPerMonth: Long,
  val basePriceUsdPerMonth: Double,
  val basePriceCoinsPerMonth: Long,
  val status: PlanStatus = PlanStatus.INACTIVE
)

object SubscriptionDurationsConfig {
  val OPTIONS = listOf(
    SubscriptionDurationOption(months = 1, labelEn = "1 Month", labelFa = "۱ ماه", discountPercent = 0, priceMultiplier = 1.0),
    SubscriptionDurationOption(months = 3, labelEn = "3 Months", labelFa = "۳ ماه", discountPercent = 10, priceMultiplier = 2.7),
    SubscriptionDurationOption(months = 6, labelEn = "6 Months", labelFa = "۶ ماه", discountPercent = 20, priceMultiplier = 4.8),
    SubscriptionDurationOption(months = 9, labelEn = "9 Months", labelFa = "۹ ماه", discountPercent = 25, priceMultiplier = 6.75),
    SubscriptionDurationOption(months = 12, labelEn = "12 Months", labelFa = "۱۲ ماه", discountPercent = 35, priceMultiplier = 7.8)
  )
}

enum class AiCreditSource {
  INITIAL_GRANT,
  DAILY_REWARD,
  PURCHASE_BUNDLE,
  COMMUNITY_BOUNTY,
  PROMO_CODE
}

enum class AiCreditType {
  GRANTED,
  CONSUMED
}

data class AiCreditTransaction(
  val transactionId: String,
  val type: AiCreditType,
  val amount: Long,
  val source: AiCreditSource,
  val timestamp: Long,
  val descriptionEn: String,
  val descriptionFa: String
)

data class AiCreditState(
  val balance: Long = 100L,
  val consumed: Long = 0L,
  val granted: Long = 100L,
  val history: List<AiCreditTransaction> = emptyList()
)

data class HeavyAiOperationCost(
  val id: String,
  val nameEn: String,
  val nameFa: String,
  val creditCost: Long,
  val descriptionEn: String,
  val descriptionFa: String
)

object HeavyAiCatalog {
  val OPERATIONS = listOf(
    HeavyAiOperationCost(
      id = "op_deep_diag",
      nameEn = "Deep Architecture Diagnostics",
      nameFa = "تحلیل عمیق معماری و ایمنی کد",
      creditCost = 15L,
      descriptionEn = "Multi-file structural AST analysis with crash-recoverable verification proposal.",
      descriptionFa = "تحلیل ساختاری چند فایلی به همراه پیشنهاد بازسازی قابل بازیابی پس از کرش."
    ),
    HeavyAiOperationCost(
      id = "op_ast_refactor",
      nameEn = "Automated Clean Code Refactoring",
      nameFa = "بازنویسی خودکار و بهینه‌سازی تمیز",
      creditCost = 25L,
      descriptionEn = "Full-file rewrite with memory and accessibility compliance checks.",
      descriptionFa = "بازنویسی کل فایل مطابق با اصول دسترسی‌پذیری و مدیریت بهینه حافظه."
    ),
    HeavyAiOperationCost(
      id = "op_spec_gen",
      nameEn = "Full Module Scaffold Generator",
      nameFa = "تولید داربست کامل ماژول نرم‌افزاری",
      creditCost = 35L,
      descriptionEn = "Generates complete ViewModel, Repository, and UI compose screens from spec.",
      descriptionFa = "تولید همزمان ویومدل، مخزن داده و صفحات Compose بر اساس مشخصات."
    )
  )
}

data class CoinEconomyConfig(
  val unitNameEn: String = "Coins",
  val unitNameFa: String = "سکه",
  val balance: Long = 350L
)

enum class PurchaseStatus {
  PENDING,
  VERIFIED,
  FAILED,
  EXPIRED,
  REFUNDED
}

enum class PaymentIdentityType {
  IRAN_LOCAL,
  OUTSIDE_IRAN
}

data class PaymentIdentity(
  val type: PaymentIdentityType,
  val gmail: String,
  val mobile: String? = null,
  val paymentDestinationRef: String? = null
)

data class PurchaseRequest(
  val purchaseId: String,
  val planTier: PlanTier,
  val durationMonths: Int,
  val paymentIdentity: PaymentIdentity,
  val amountFormatted: String,
  val currency: String
)

data class PurchaseResult(
  val purchaseId: String,
  val status: PurchaseStatus,
  val providerId: String,
  val trackingCode: String?,
  val rawStatusMessage: String
)

data class VerificationResult(
  val purchaseId: String,
  val status: PurchaseStatus,
  val isVerified: Boolean,
  val activatedTier: PlanTier?,
  val expiresAt: Long?,
  val statusMessage: String
)

data class RestoreResult(
  val isSuccess: Boolean,
  val restoredTier: PlanTier,
  val activeUntil: Long?,
  val restoredCredits: Long,
  val message: String
)
