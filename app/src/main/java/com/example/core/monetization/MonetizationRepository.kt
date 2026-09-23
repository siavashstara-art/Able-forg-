package com.example.core.monetization

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class MonetizationRepository(
  val iranProvider: PaymentProvider = IranLocalPaymentProvider(),
  val intlProvider: PaymentProvider = InternationalPaymentProvider()
) {

  // Current active subscription tier (Free by default)
  private val _currentTier = MutableStateFlow(PlanTier.FREE)
  val currentTier: StateFlow<PlanTier> = _currentTier.asStateFlow()

  private val _subscriptionExpiry = MutableStateFlow<Long?>(null)
  val subscriptionExpiry: StateFlow<Long?> = _subscriptionExpiry.asStateFlow()

  // Selected duration for comparison/pricing (defaults to 1 month)
  private val _selectedDurationMonths = MutableStateFlow(1)
  val selectedDurationMonths: StateFlow<Int> = _selectedDurationMonths.asStateFlow()

  // Active Payment Identity
  private val _paymentIdentity = MutableStateFlow(
    PaymentIdentity(
      type = PaymentIdentityType.IRAN_LOCAL,
      gmail = "developer@ableforge.dev",
      mobile = "09120000000"
    )
  )
  val paymentIdentity: StateFlow<PaymentIdentity> = _paymentIdentity.asStateFlow()

  // AI Credits (Decoupled from Subscription)
  private val _aiCredits = MutableStateFlow(
    AiCreditState(
      balance = 120L,
      consumed = 15L,
      granted = 135L,
      history = listOf(
        AiCreditTransaction(
          transactionId = "tx_ai_init",
          type = AiCreditType.GRANTED,
          amount = 100L,
          source = AiCreditSource.INITIAL_GRANT,
          timestamp = System.currentTimeMillis() - (7L * 86400000L),
          descriptionEn = "Welcome AI Starter Grant",
          descriptionFa = "اعتبار هدیه شروع به کار در کارگاه توانا"
        ),
        AiCreditTransaction(
          transactionId = "tx_ai_diag_1",
          type = AiCreditType.CONSUMED,
          amount = 15L,
          source = AiCreditSource.INITIAL_GRANT,
          timestamp = System.currentTimeMillis() - (2L * 86400000L),
          descriptionEn = "Deep Architecture Diagnostics execution",
          descriptionFa = "اجرای تحلیل عمیق خطایابی معماری"
        ),
        AiCreditTransaction(
          transactionId = "tx_ai_daily",
          type = AiCreditType.GRANTED,
          amount = 35L,
          source = AiCreditSource.DAILY_REWARD,
          timestamp = System.currentTimeMillis() - 3600000L,
          descriptionEn = "Daily Active Coder AI Allowance",
          descriptionFa = "سهمیه روزانه کدنویس فعال"
        )
      )
    )
  )
  val aiCredits: StateFlow<AiCreditState> = _aiCredits.asStateFlow()

  // In-App Economy Tokens (Coins / Stars / Units - Decoupled from Subscriptions)
  private val _coinEconomy = MutableStateFlow(
    CoinEconomyConfig(
      unitNameEn = "Coins",
      unitNameFa = "سکه",
      balance = 450L
    )
  )
  val coinEconomy: StateFlow<CoinEconomyConfig> = _coinEconomy.asStateFlow()

  // Active or last purchase transaction state
  private val _lastPurchaseResult = MutableStateFlow<PurchaseResult?>(null)
  val lastPurchaseResult: StateFlow<PurchaseResult?> = _lastPurchaseResult.asStateFlow()

  private val _lastVerificationResult = MutableStateFlow<VerificationResult?>(null)
  val lastVerificationResult: StateFlow<VerificationResult?> = _lastVerificationResult.asStateFlow()

  private val _restoreMessage = MutableStateFlow<String?>(null)
  val restoreMessage: StateFlow<String?> = _restoreMessage.asStateFlow()

  val availablePlans: List<SubscriptionPlan> = listOf(
    SubscriptionPlan(
      id = "plan_free",
      tier = PlanTier.FREE,
      nameEn = "Free Core",
      nameFa = "نسخه پایه رایگان",
      featuresEn = listOf(
        "Standard Code Editor with syntax highlighting",
        "Local Room database persistence",
        "Rule-based deterministic AI Fixes",
        "Speech synthesis (TTS) & STT voice input",
        "Full offline accessibility & traffic light guidance",
        "Community Discussion Rooms & Participation"
      ),
      featuresFa = listOf(
        "ویرایشگر کامل کد با رنگ‌آمیزی ساختار",
        "پایگاه‌داده محلی پایدار بدون نیاز به اینترنت",
        "اصلاح‌گر ساختارمند مبتنی بر قواعد قطعی",
        "خوانش صوتی محلی و ورودی گفتار به متن",
        "سیستم راهنمای هوشمند چراغ راهنما و دسترس‌پذیری کامل",
        "اتاق‌های گفت‌وگو و مشارکت در انجمن برنامه‌نویسان"
      ),
      limitsEn = mapOf(
        "Max Projects" to "10 Projects",
        "Max Files per Project" to "100 Files",
        "Concurrent Git Sync" to "1 Active Repo"
      ),
      limitsFa = mapOf(
        "حداکثر پروژه‌ها" to "۱۰ پروژه",
        "حداکثر فایل هر پروژه" to "۱۰۰ فایل",
        "همگام‌سازی گیت" to "۱ مخزن همزمان"
      ),
      basePriceTomanPerMonth = 0L,
      basePriceUsdPerMonth = 0.0,
      basePriceCoinsPerMonth = 0L,
      status = PlanStatus.ACTIVE
    ),
    SubscriptionPlan(
      id = "plan_premium",
      tier = PlanTier.PREMIUM,
      nameEn = "ABLE Premium",
      nameFa = "اشتراک ویژه (توانا پرمیوم)",
      featuresEn = listOf(
        "Unlimited Projects and Project Files",
        "Full Git Index Staging, Remote Push & Conflict Alerts",
        "Crash-Recoverable Multi-file AST Refactoring",
        "Priority Voice Synthesis with custom speed control",
        "Enhanced Project Showcases with badge highlight",
        "500 Bonus Monthly AI Diagnostic Credits"
      ),
      featuresFa = listOf(
        "پروژه‌ها و فایل‌های نامحدود بدون سقف محلی",
        "ارسال مستقیم و دریافت گیت به مخازن دوردست با هشدار تداخل",
        "بازنویسی چندفایلی هوشمند با امکان بازیابی امن پس از کرش",
        "خوانش صوتی اختصاصی با تنظیم پیشرفته سرعت گویش",
        "برجسته‌سازی ویترین پروژه‌ها در انجمن با نشان تأیید",
        "۵۰۰ واحد اعتبار ماهانه تحلیل هوش مصنوعی"
      ),
      limitsEn = mapOf(
        "Max Projects" to "Unlimited",
        "Max Files per Project" to "Unlimited",
        "Concurrent Git Sync" to "5 Active Repos"
      ),
      limitsFa = mapOf(
        "حداکثر پروژه‌ها" to "نامحدود",
        "حداکثر فایل هر پروژه" to "نامحدود",
        "همگام‌سازی گیت" to "۵ مخزن همزمان"
      ),
      basePriceTomanPerMonth = 190000L,
      basePriceUsdPerMonth = 9.99,
      basePriceCoinsPerMonth = 180L,
      status = PlanStatus.INACTIVE
    ),
    SubscriptionPlan(
      id = "plan_plus",
      tier = PlanTier.PLUS,
      nameEn = "ABLE Plus Pro",
      nameFa = "اشتراک پلاس حرفه‌ای (توانا پلاس)",
      featuresEn = listOf(
        "All Premium features included",
        "Automated Architecture & Full Module Scaffolder",
        "Deep AST Static Safety Analyzer with Memory Leaks Check",
        "Multi-repo concurrent sync pipeline",
        "Priority APK/AAB build worker queue",
        "2,000 Bonus Monthly AI Diagnostic Credits"
      ),
      featuresFa = listOf(
        "شامل تمام قابلیت‌های سطح پرمیوم",
        "تولید خودکار معماری و داربست‌بندی ساختار ماژولار",
        "تحلیل عمیق ایمنی استاتیک و جلوگیری از نشت حافظه",
        "خط لوله همگام‌سازی چندمخزنه پیشرفته",
        "اولویت بالا در صف ساخت بسته نصبی APK و AAB",
        "۲۰۰۰ واحد اعتبار ماهانه تحلیل هوش مصنوعی"
      ),
      limitsEn = mapOf(
        "Max Projects" to "Unlimited",
        "Max Files per Project" to "Unlimited",
        "Concurrent Git Sync" to "20 Active Repos"
      ),
      limitsFa = mapOf(
        "حداکثر پروژه‌ها" to "نامحدود",
        "حداکثر فایل هر پروژه" to "نامحدود",
        "همگام‌سازی گیت" to "۲۰ مخزن همزمان"
      ),
      basePriceTomanPerMonth = 390000L,
      basePriceUsdPerMonth = 24.99,
      basePriceCoinsPerMonth = 350L,
      status = PlanStatus.INACTIVE
    )
  )

  fun selectDurationMonths(months: Int) {
    if (SubscriptionDurationsConfig.OPTIONS.any { it.months == months }) {
      _selectedDurationMonths.value = months
    }
  }

  fun updatePaymentIdentity(identity: PaymentIdentity) {
    _paymentIdentity.value = identity
  }

  fun setCoinUnitName(unitNameEn: String, unitNameFa: String) {
    _coinEconomy.value = _coinEconomy.value.copy(
      unitNameEn = unitNameEn,
      unitNameFa = unitNameFa
    )
  }

  /**
   * Transparent Heavy AI cost verification before consumption.
   * Returns false if balance is insufficient.
   */
  fun canAffordAiOperation(creditCost: Long): Boolean {
    return _aiCredits.value.balance >= creditCost
  }

  /**
   * Executes AI Credit consumption transparently.
   * Never hides credit cost or executes secretly.
   */
  fun consumeAiCredits(operation: HeavyAiOperationCost): Result<Long> {
    val current = _aiCredits.value
    if (current.balance < operation.creditCost) {
      return Result.failure(
        IllegalStateException("اعتبار هوش مصنوعی ناکافی است. موجودی: ${current.balance}، مورد نیاز: ${operation.creditCost}")
      )
    }

    val updatedBalance = current.balance - operation.creditCost
    val updatedConsumed = current.consumed + operation.creditCost
    val transaction = AiCreditTransaction(
      transactionId = "tx_ai_${UUID.randomUUID().toString().take(8)}",
      type = AiCreditType.CONSUMED,
      amount = operation.creditCost,
      source = AiCreditSource.INITIAL_GRANT,
      timestamp = System.currentTimeMillis(),
      descriptionEn = "Executed ${operation.nameEn}",
      descriptionFa = "اجرای عملیات: ${operation.nameFa}"
    )

    _aiCredits.value = current.copy(
      balance = updatedBalance,
      consumed = updatedConsumed,
      history = listOf(transaction) + current.history
    )

    return Result.success(updatedBalance)
  }

  fun grantAiCredits(amount: Long, source: AiCreditSource, descriptionEn: String, descriptionFa: String) {
    val current = _aiCredits.value
    val transaction = AiCreditTransaction(
      transactionId = "tx_ai_${UUID.randomUUID().toString().take(8)}",
      type = AiCreditType.GRANTED,
      amount = amount,
      source = source,
      timestamp = System.currentTimeMillis(),
      descriptionEn = descriptionEn,
      descriptionFa = descriptionFa
    )
    _aiCredits.value = current.copy(
      balance = current.balance + amount,
      granted = current.granted + amount,
      history = listOf(transaction) + current.history
    )
  }

  /**
   * Purchases plan with In-App Coins/Stars tokens.
   */
  fun purchaseWithCoins(tier: PlanTier, durationMonths: Int): Result<String> {
    val plan = availablePlans.find { it.tier == tier }
      ?: return Result.failure(IllegalArgumentException("پلن نامعتبر است"))

    val option = SubscriptionDurationsConfig.OPTIONS.find { it.months == durationMonths }
      ?: SubscriptionDurationsConfig.OPTIONS.first()

    val totalCoinCost = (plan.basePriceCoinsPerMonth * option.priceMultiplier).toLong()
    val currentCoins = _coinEconomy.value.balance

    if (currentCoins < totalCoinCost) {
      return Result.failure(
        IllegalStateException("موجودی ${_coinEconomy.value.unitNameFa} ناکافی است. موجودی: $currentCoins، هزینه: $totalCoinCost")
      )
    }

    // Deduct coins
    _coinEconomy.value = _coinEconomy.value.copy(balance = currentCoins - totalCoinCost)

    // Activate Entitlement
    _currentTier.value = tier
    val durationMillis = durationMonths * 30L * 86400000L
    _subscriptionExpiry.value = System.currentTimeMillis() + durationMillis

    return Result.success("خرید اشتراک ${plan.nameFa} با موفقیت توسط ${_coinEconomy.value.unitNameFa} انجام شد.")
  }

  suspend fun initiatePurchase(
    plan: SubscriptionPlan,
    durationMonths: Int,
    identity: PaymentIdentity
  ): PurchaseResult {
    val option = SubscriptionDurationsConfig.OPTIONS.find { it.months == durationMonths }
      ?: SubscriptionDurationsConfig.OPTIONS.first()

    val isIran = identity.type == PaymentIdentityType.IRAN_LOCAL
    val provider = if (isIran) iranProvider else intlProvider

    val amountFormatted = if (isIran) {
      val totalToman = (plan.basePriceTomanPerMonth * option.priceMultiplier).toLong()
      "%,d تومان".format(totalToman)
    } else {
      val totalUsd = plan.basePriceUsdPerMonth * option.priceMultiplier
      "$%.2f USD".format(totalUsd)
    }

    val request = PurchaseRequest(
      purchaseId = "PUR-${UUID.randomUUID().toString().take(8).uppercase()}",
      planTier = plan.tier,
      durationMonths = durationMonths,
      paymentIdentity = identity,
      amountFormatted = amountFormatted,
      currency = if (isIran) "TOMAN" else "USD"
    )

    val result = provider.createPurchase(request)
    _lastPurchaseResult.value = result
    return result
  }

  suspend fun verifyActivePurchase(purchaseId: String, trackingToken: String): VerificationResult {
    val isIran = _paymentIdentity.value.type == PaymentIdentityType.IRAN_LOCAL
    val provider = if (isIran) iranProvider else intlProvider

    val verification = provider.verifyPurchase(purchaseId, trackingToken)
    _lastVerificationResult.value = verification

    if (verification.isVerified && verification.activatedTier != null) {
      _currentTier.value = verification.activatedTier
      _subscriptionExpiry.value = verification.expiresAt
    }

    return verification
  }

  suspend fun restoreEntitlements(): RestoreResult {
    val isIran = _paymentIdentity.value.type == PaymentIdentityType.IRAN_LOCAL
    val provider = if (isIran) iranProvider else intlProvider

    val result = provider.restorePurchase(_paymentIdentity.value)
    _restoreMessage.value = result.message

    if (result.isSuccess && result.restoredTier != PlanTier.FREE) {
      _currentTier.value = result.restoredTier
      _subscriptionExpiry.value = result.activeUntil
      if (result.restoredCredits > 0) {
        grantAiCredits(
          result.restoredCredits,
          AiCreditSource.PURCHASE_BUNDLE,
          "Restored Entitlement AI Credits",
          "اعتبار هوش مصنوعی بازیابی‌شده از اشتراک معتبر"
        )
      }
    }

    return result
  }

  /**
   * Evaluates subscription expiration.
   * Gating: Reverts tier to FREE when expired, but NEVER touches user data.
   */
  fun checkEntitlementExpiration() {
    val expiry = _subscriptionExpiry.value
    if (expiry != null && System.currentTimeMillis() > expiry) {
      _currentTier.value = PlanTier.FREE
      _subscriptionExpiry.value = null
    }
  }
}
