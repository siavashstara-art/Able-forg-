package com.example.features.monetization

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.localization.ForgeLanguage
import com.example.core.monetization.AiCreditSource
import com.example.core.monetization.AiCreditState
import com.example.core.monetization.AiCreditType
import com.example.core.monetization.CoinEconomyConfig
import com.example.core.monetization.HeavyAiCatalog
import com.example.core.monetization.HeavyAiOperationCost
import com.example.core.monetization.PaymentIdentity
import com.example.core.monetization.PaymentIdentityType
import com.example.core.monetization.PlanTier
import com.example.core.monetization.PurchaseResult
import com.example.core.monetization.PurchaseStatus
import com.example.core.monetization.SubscriptionDurationOption
import com.example.core.monetization.SubscriptionDurationsConfig
import com.example.core.monetization.SubscriptionPlan
import com.example.core.monetization.VerificationResult
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MonetizationScreen(
  currentTier: PlanTier,
  subscriptionExpiry: Long?,
  selectedDurationMonths: Int,
  availablePlans: List<SubscriptionPlan>,
  aiCredits: AiCreditState,
  coinEconomy: CoinEconomyConfig,
  paymentIdentity: PaymentIdentity,
  lastPurchaseResult: PurchaseResult?,
  lastVerificationResult: VerificationResult?,
  restoreMessage: String?,
  language: ForgeLanguage,
  onSelectDuration: (Int) -> Unit,
  onInitiatePurchase: (SubscriptionPlan, Int, PaymentIdentity) -> Unit,
  onPurchaseWithCoins: (PlanTier, Int) -> Unit,
  onConsumeHeavyAi: (HeavyAiOperationCost) -> Unit,
  onRestorePurchases: () -> Unit,
  onUpdateIdentity: (PaymentIdentity) -> Unit,
  onSelectCoinUnitName: (String, String) -> Unit,
  modifier: Modifier = Modifier
) {
  val isFa = language == ForgeLanguage.FA
  var selectedTab by remember { mutableIntStateOf(0) }

  // State for AI Credit Cost Confirmation Dialog
  var pendingAiOperation by remember { mutableStateOf<HeavyAiOperationCost?>(null) }
  var pendingPurchasePlan by remember { mutableStateOf<SubscriptionPlan?>(null) }

  val tabs = listOf(
    if (isFa) "پلن‌های اشتراک" else "Subscription Plans",
    if (isFa) "اعتبار هوش مصنوعی" else "AI Credits",
    if (isFa) "واحدهای داخلی (${coinEconomy.unitNameFa})" else "In-App Tokens (${coinEconomy.unitNameEn})",
    if (isFa) "هویت پرداخت و بازیابی" else "Payment Identity & Restore"
  )

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
  ) {
    // 1. Top Header & Current Entitlement Status
    Surface(
      color = MaterialTheme.colorScheme.surface,
      tonalElevation = 2.dp,
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 12.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = if (isFa) "طرح‌های اشتراک و اعتبارات" else "Monetization & Entitlements",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = if (isFa) "سیستم مستقل اشتراک، اعتبار هوش مصنوعی و واحدهای داخلی" else "Decoupled subscriptions, transparent AI credits & tokens",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          // Active Tier Badge
          val tierColor = when (currentTier) {
            PlanTier.FREE -> MaterialTheme.colorScheme.secondary
            PlanTier.PREMIUM -> MaterialTheme.colorScheme.primary
            PlanTier.PLUS -> Color(0xFFD97706) // Amber / Gold
          }
          Surface(
            color = tierColor.copy(alpha = 0.15f),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, tierColor)
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Shield,
                contentDescription = null,
                tint = tierColor,
                modifier = Modifier.size(16.dp)
              )
              Text(
                text = currentTier.name,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = tierColor
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Truthful Backend Disclaimer Bar
        Surface(
          color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Warning,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.error,
              modifier = Modifier.size(16.dp)
            )
            Text(
              text = if (isFa)
                "وضعیت سرور پرداخت: متصل نیست / تایید نشده (NOT CONNECTED / NOT VERIFIED) — موفقیت جعلی ممنوع است."
              else
                "Server Connection Status: NOT CONNECTED / NOT VERIFIED — Simulated successes are prohibited.",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onErrorContainer,
              fontWeight = FontWeight.Medium
            )
          }
        }
      }
    }

    // 2. Navigation Tabs
    ScrollableTabRow(
      selectedTabIndex = selectedTab,
      edgePadding = 16.dp,
      containerColor = MaterialTheme.colorScheme.surface,
      contentColor = MaterialTheme.colorScheme.primary,
      modifier = Modifier.fillMaxWidth()
    ) {
      tabs.forEachIndexed { index, title ->
        Tab(
          selected = selectedTab == index,
          onClick = { selectedTab = index },
          text = {
            Text(
              text = title,
              fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
              style = MaterialTheme.typography.bodySmall
            )
          },
          modifier = Modifier.testTag("monetization_tab_$index")
        )
      }
    }

    // 3. Tab Contents
    Box(modifier = Modifier.weight(1f)) {
      when (selectedTab) {
        0 -> PlansTabContent(
          currentTier = currentTier,
          selectedDurationMonths = selectedDurationMonths,
          availablePlans = availablePlans,
          coinEconomy = coinEconomy,
          paymentIdentity = paymentIdentity,
          isFa = isFa,
          onSelectDuration = onSelectDuration,
          onSelectPlanForPurchase = { plan -> pendingPurchasePlan = plan },
          onPurchaseWithCoins = onPurchaseWithCoins
        )
        1 -> AiCreditsTabContent(
          aiCredits = aiCredits,
          isFa = isFa,
          onSelectOperation = { op -> pendingAiOperation = op }
        )
        2 -> InAppTokensTabContent(
          coinEconomy = coinEconomy,
          isFa = isFa,
          onSelectCoinUnitName = onSelectCoinUnitName
        )
        3 -> PaymentIdentityTabContent(
          paymentIdentity = paymentIdentity,
          restoreMessage = restoreMessage,
          lastPurchaseResult = lastPurchaseResult,
          lastVerificationResult = lastVerificationResult,
          isFa = isFa,
          onUpdateIdentity = onUpdateIdentity,
          onRestorePurchases = onRestorePurchases
        )
      }
    }

    // 4. Policy & Legal Disclaimer Footer
    Surface(
      color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Icon(
          imageVector = Icons.Default.Info,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.onSurfaceVariant,
          modifier = Modifier.size(16.dp)
        )
        Text(
          text = if (isFa)
            "امتیاز XP، لیگ‌ها و خرید اشتراک صرفاً ابزار مشارکت و بهره‌وری هستند؛ خرید هرگز متضمن دریافت زمین، استخدام، سمت مدیریتی یا کرسی شورا نیست."
          else
            "XP, Leagues, and Subscriptions are participation & productivity features only; purchases do not guarantee titles, land, employment, or council seats.",
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }
  }

  // --- Dialog 1: Transparent Heavy AI Operation Cost Confirmation ---
  pendingAiOperation?.let { op ->
    AlertDialog(
      onDismissRequest = { pendingAiOperation = null },
      title = {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
          Text(text = if (isFa) "تأیید کسر اعتبار هوش مصنوعی" else "Confirm AI Credit Consumption")
        }
      },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Text(
            text = if (isFa)
              "پیش از اجرای عملیات سنگین، مصرف شفاف اعتبار به شما نمایش داده می‌شود:"
            else
              "Transparent cost preview before executing heavy AI operation:",
            style = MaterialTheme.typography.bodyMedium
          )

          Surface(
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
              Text(
                text = if (isFa) op.nameFa else op.nameEn,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyMedium
              )
              Text(
                text = if (isFa) op.descriptionFa else op.descriptionEn,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
              HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text(text = if (isFa) "هزینه عملیات:" else "Operation Cost:", style = MaterialTheme.typography.bodySmall)
                Text(
                  text = "${op.creditCost} ${if (isFa) "واحد اعتبار" else "Credits"}",
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.primary
                )
              }
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text(text = if (isFa) "موجودی فعلی شما:" else "Your Current Balance:", style = MaterialTheme.typography.bodySmall)
                Text(text = "${aiCredits.balance} ${if (isFa) "واحد" else "Credits"}", fontWeight = FontWeight.Bold)
              }
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text(text = if (isFa) "موجودی پس از کسر:" else "Remaining Balance:", style = MaterialTheme.typography.bodySmall)
                Text(
                  text = "${aiCredits.balance - op.creditCost} ${if (isFa) "واحد" else "Credits"}",
                  fontWeight = FontWeight.Bold,
                  color = if (aiCredits.balance >= op.creditCost) Color(0xFF16A34A) else MaterialTheme.colorScheme.error
                )
              }
            }
          }

          if (aiCredits.balance < op.creditCost) {
            Text(
              text = if (isFa) "اعتبار ناکافی است. لطفاً بسته اعتباری تهیه فرمایید." else "Insufficient credits. Please grant or acquire additional credits.",
              color = MaterialTheme.colorScheme.error,
              style = MaterialTheme.typography.labelSmall
            )
          }
        }
      },
      confirmButton = {
        Button(
          onClick = {
            onConsumeHeavyAi(op)
            pendingAiOperation = null
          },
          enabled = aiCredits.balance >= op.creditCost,
          modifier = Modifier.testTag("confirm_ai_consumption_btn")
        ) {
          Text(text = if (isFa) "تأیید و اجرای عملیات" else "Confirm & Execute")
        }
      },
      dismissButton = {
        TextButton(onClick = { pendingAiOperation = null }) {
          Text(text = if (isFa) "انصراف" else "Cancel")
        }
      }
    )
  }

  // --- Dialog 2: Purchase Initiation Confirmation ---
  pendingPurchasePlan?.let { plan ->
    val durationOption = SubscriptionDurationsConfig.OPTIONS.find { it.months == selectedDurationMonths }
      ?: SubscriptionDurationsConfig.OPTIONS.first()
    val isIran = paymentIdentity.type == PaymentIdentityType.IRAN_LOCAL

    val formattedPrice = if (isIran) {
      val total = (plan.basePriceTomanPerMonth * durationOption.priceMultiplier).toLong()
      "%,d تومان".format(total)
    } else {
      val total = plan.basePriceUsdPerMonth * durationOption.priceMultiplier
      "$%.2f USD".format(total)
    }

    AlertDialog(
      onDismissRequest = { pendingPurchasePlan = null },
      title = {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Icon(Icons.Default.CreditCard, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
          Text(text = if (isFa) "پیش‌فاکتور و تایید خرید" else "Purchase Invoice & Confirmation")
        }
      },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Text(
            text = if (isFa)
              "مشخصات اشتراک انتخابی بر اساس مدت زمان و هویت پرداخت:"
            else
              "Subscription details based on duration and payment identity:",
            style = MaterialTheme.typography.bodyMedium
          )

          Surface(
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = if (isFa) "پلن:" else "Plan:", style = MaterialTheme.typography.bodySmall)
                Text(text = if (isFa) plan.nameFa else plan.nameEn, fontWeight = FontWeight.Bold)
              }
              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = if (isFa) "مدت اشتراک:" else "Duration:", style = MaterialTheme.typography.bodySmall)
                Text(text = if (isFa) durationOption.labelFa else durationOption.labelEn, fontWeight = FontWeight.Bold)
              }
              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = if (isFa) "مبلغ نهایی:" else "Total Price:", style = MaterialTheme.typography.bodySmall)
                Text(text = formattedPrice, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
              }
              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = if (isFa) "روش پرداخت:" else "Payment Gateway:", style = MaterialTheme.typography.bodySmall)
                Text(
                  text = if (isIran) (if (isFa) "شبکه شتاب / شاپرک" else "Iran Shetab/Shaparak") else (if (isFa) "درگاه بین‌المللی" else "International Destination"),
                  fontWeight = FontWeight.Medium
                )
              }
              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = if (isFa) "شناسه حساب:" else "Account ID:", style = MaterialTheme.typography.bodySmall)
                Text(text = paymentIdentity.mobile ?: paymentIdentity.gmail, style = MaterialTheme.typography.labelSmall)
              }
            }
          }

          Surface(
            color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f),
            shape = RoundedCornerShape(6.dp)
          ) {
            Text(
              text = if (isFa)
                "توجه: سرور پرداخت متصل نیست (NOT CONNECTED / NOT VERIFIED). پس از ارسال، تراکنش در وضعیت Pending باقی می‌ماند و موفقیت ساختگی ایجاد نمی‌گردد."
              else
                "Note: Payment server is NOT CONNECTED / NOT VERIFIED. Transaction will be marked Pending without mock success.",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onErrorContainer,
              modifier = Modifier.padding(8.dp)
            )
          }
        }
      },
      confirmButton = {
        Button(
          onClick = {
            onInitiatePurchase(plan, selectedDurationMonths, paymentIdentity)
            pendingPurchasePlan = null
          },
          modifier = Modifier.testTag("submit_purchase_btn")
        ) {
          Text(text = if (isFa) "ثبت درخواست درگاه" else "Submit Gateway Request")
        }
      },
      dismissButton = {
        TextButton(onClick = { pendingPurchasePlan = null }) {
          Text(text = if (isFa) "انصراف" else "Cancel")
        }
      }
    )
  }
}

// -------------------------------------------------------------------------------------------------
// TAB 1: PLANS TAB CONTENT
// -------------------------------------------------------------------------------------------------
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PlansTabContent(
  currentTier: PlanTier,
  selectedDurationMonths: Int,
  availablePlans: List<SubscriptionPlan>,
  coinEconomy: CoinEconomyConfig,
  paymentIdentity: PaymentIdentity,
  isFa: Boolean,
  onSelectDuration: (Int) -> Unit,
  onSelectPlanForPurchase: (SubscriptionPlan) -> Unit,
  onPurchaseWithCoins: (PlanTier, Int) -> Unit
) {
  val isIran = paymentIdentity.type == PaymentIdentityType.IRAN_LOCAL

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // 1. Configuration-driven Duration Selector
    item {
      Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Text(
            text = if (isFa) "انتخاب مدت زمان اشتراک (Configuration-Driven)" else "Select Subscription Duration",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
          )
          Text(
            text = if (isFa)
              "مدت‌زمان‌ها به صورت پویا از تنظیمات دریافت می‌شوند (۱، ۳، ۶، ۹ و ۱۲ ماهه):"
            else
              "Durations are driven by configuration options (1, 3, 6, 9, 12 months):",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 8.dp)
          )

          FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            SubscriptionDurationsConfig.OPTIONS.forEach { opt ->
              val isSelected = opt.months == selectedDurationMonths
              FilterChip(
                selected = isSelected,
                onClick = { onSelectDuration(opt.months) },
                label = {
                  Text(
                    text = if (isFa) opt.labelFa else opt.labelEn,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                  )
                },
                leadingIcon = if (opt.discountPercent > 0) {
                  {
                    Surface(
                      color = MaterialTheme.colorScheme.errorContainer,
                      shape = RoundedCornerShape(4.dp)
                    ) {
                      Text(
                        text = "-${opt.discountPercent}%",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                      )
                    }
                  }
                } else null,
                colors = FilterChipDefaults.filterChipColors(
                  selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                  selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                modifier = Modifier.testTag("duration_chip_${opt.months}")
              )
            }
          }
        }
      }
    }

    // 2. Plan Comparison Cards
    items(availablePlans) { plan ->
      val isCurrentPlan = currentTier == plan.tier
      val durationOption = SubscriptionDurationsConfig.OPTIONS.find { it.months == selectedDurationMonths }
        ?: SubscriptionDurationsConfig.OPTIONS.first()

      val tomanPrice = (plan.basePriceTomanPerMonth * durationOption.priceMultiplier).toLong()
      val usdPrice = plan.basePriceUsdPerMonth * durationOption.priceMultiplier
      val coinPrice = (plan.basePriceCoinsPerMonth * durationOption.priceMultiplier).toLong()

      Card(
        colors = CardDefaults.cardColors(
          containerColor = if (isCurrentPlan)
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
          else
            MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
          width = if (isCurrentPlan) 2.dp else 1.dp,
          color = if (isCurrentPlan) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("plan_card_${plan.tier.name.lowercase()}")
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          // Plan Header
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = if (isFa) plan.nameFa else plan.nameEn,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "${if (isFa) durationOption.labelFa else durationOption.labelEn} (${if (isFa) "محاسبه بر اساس مدت" else "Dynamic pricing"})",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }

            if (isCurrentPlan) {
              Surface(
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(8.dp)
              ) {
                Text(
                  text = if (isFa) "پلن فعال شما" else "ACTIVE PLAN",
                  color = MaterialTheme.colorScheme.onPrimary,
                  style = MaterialTheme.typography.labelSmall,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Price Row
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Column {
              if (plan.tier == PlanTier.FREE) {
                Text(
                  text = if (isFa) "رایگان همیشگی" else "100% Free",
                  style = MaterialTheme.typography.headlineSmall,
                  fontWeight = FontWeight.ExtraBold,
                  color = MaterialTheme.colorScheme.primary
                )
              } else {
                Text(
                  text = if (isIran) "%,d تومان".format(tomanPrice) else "$%.2f USD".format(usdPrice),
                  style = MaterialTheme.typography.headlineSmall,
                  fontWeight = FontWeight.ExtraBold,
                  color = MaterialTheme.colorScheme.primary
                )
                Text(
                  text = "${if (isIran) "$%.2f USD".format(usdPrice) else "%,d تومان".format(tomanPrice)} | $coinPrice ${coinEconomy.unitNameFa}",
                  style = MaterialTheme.typography.labelSmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
          }

          HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

          // Features List
          Text(
            text = if (isFa) "امکانات و قابلیت‌ها:" else "Features included:",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold
          )
          Spacer(modifier = Modifier.height(6.dp))

          val features = if (isFa) plan.featuresFa else plan.featuresEn
          features.forEach { feat ->
            Row(
              modifier = Modifier.padding(vertical = 2.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = Color(0xFF16A34A),
                modifier = Modifier.size(16.dp)
              )
              Text(text = feat, style = MaterialTheme.typography.bodySmall)
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Limits List
          Text(
            text = if (isFa) "محدودیت‌ها و سقف استفاده:" else "Plan limits:",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          val limits = if (isFa) plan.limitsFa else plan.limitsEn
          limits.forEach { (k, v) ->
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 1.dp),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(text = k, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
              Text(text = v, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Action Buttons
          if (plan.tier != PlanTier.FREE) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Button(
                onClick = { onSelectPlanForPurchase(plan) },
                modifier = Modifier
                  .weight(1f)
                  .testTag("purchase_gateway_${plan.tier.name.lowercase()}"),
                colors = ButtonDefaults.buttonColors(
                  containerColor = MaterialTheme.colorScheme.primary
                )
              ) {
                Icon(Icons.Default.CreditCard, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = if (isIran) (if (isFa) "پرداخت شتاب" else "Pay via Shetab") else (if (isFa) "پرداخت بین‌المللی" else "Pay Intl"),
                  style = MaterialTheme.typography.labelMedium
                )
              }

              OutlinedButton(
                onClick = { onPurchaseWithCoins(plan.tier, selectedDurationMonths) },
                modifier = Modifier
                  .weight(1f)
                  .testTag("purchase_coins_${plan.tier.name.lowercase()}")
              ) {
                Icon(Icons.Default.Paid, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = if (isFa) "خرید با ${coinEconomy.unitNameFa}" else "Buy with ${coinEconomy.unitNameEn}",
                  style = MaterialTheme.typography.labelMedium
                )
              }
            }
          }
        }
      }
    }
  }
}

// -------------------------------------------------------------------------------------------------
// TAB 2: AI CREDITS TAB CONTENT (Decoupled from Subscriptions)
// -------------------------------------------------------------------------------------------------
@Composable
private fun AiCreditsTabContent(
  aiCredits: AiCreditState,
  isFa: Boolean,
  onSelectOperation: (HeavyAiOperationCost) -> Unit
) {
  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // 1. Balance Summary Card
    item {
      Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = if (isFa) "موجودی اعتبار هوش مصنوعی (مستقل از اشتراک)" else "AI Credits Balance (Decoupled)",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = if (isFa) "اعتبارات مصرفی و باقیمانده بر اساس تراکنش‌های ثبت‌شده" else "Recorded balance and transparent consumption history",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }

            Surface(
              color = MaterialTheme.colorScheme.primaryContainer,
              shape = CircleShape
            ) {
              Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier
                  .padding(10.dp)
                  .size(24.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Column {
              Text(text = if (isFa) "موجودی در دسترس" else "Available", style = MaterialTheme.typography.labelSmall)
              Text(
                text = "${aiCredits.balance}",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
              )
            }
            Column {
              Text(text = if (isFa) "کل مصرف‌شده" else "Consumed", style = MaterialTheme.typography.labelSmall)
              Text(
                text = "${aiCredits.consumed}",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
            Column {
              Text(text = if (isFa) "کل اعطا شده" else "Granted", style = MaterialTheme.typography.labelSmall)
              Text(
                text = "${aiCredits.granted}",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF16A34A)
              )
            }
          }
        }
      }
    }

    // 2. Transparent Heavy AI Catalog & Cost Estimator
    item {
      Text(
        text = if (isFa) "فهرست و برآورد هزینه عملیات سنگین هوش مصنوعی" else "Heavy AI Operations & Transparent Cost Estimator",
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 4.dp)
      )
      Text(
        text = if (isFa)
          "هیچ عملیاتی با هزینه پنهان اجرا نخواهد شد. پیش از مصرف، هزینه به صورت شفاف اعلام و تایید می‌شود:"
        else
          "No hidden AI execution. Costs are shown and confirmed transparently prior to running:",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }

    items(HeavyAiCatalog.OPERATIONS) { op ->
      Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = if (isFa) op.nameFa else op.nameEn,
              fontWeight = FontWeight.Bold,
              style = MaterialTheme.typography.bodyMedium
            )
            Text(
              text = if (isFa) op.descriptionFa else op.descriptionEn,
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          Spacer(modifier = Modifier.width(12.dp))

          Column(horizontalAlignment = Alignment.End) {
            Surface(
              color = MaterialTheme.colorScheme.secondaryContainer,
              shape = RoundedCornerShape(8.dp)
            ) {
              Text(
                text = "${op.creditCost} ${if (isFa) "اعتبار" else "Credits"}",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Button(
              onClick = { onSelectOperation(op) },
              contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
              modifier = Modifier.testTag("preview_op_${op.id}")
            ) {
              Text(text = if (isFa) "برآورد و تایید" else "Preview & Run", style = MaterialTheme.typography.labelSmall)
            }
          }
        }
      }
    }

    // 3. Transactions History
    item {
      Text(
        text = if (isFa) "دفترچه تراکنش‌های اعتبار (Ledger)" else "Credits Transaction History",
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 8.dp)
      )
    }

    items(aiCredits.history) { tx ->
      val isGranted = tx.type == AiCreditType.GRANTED
      val color = if (isGranted) Color(0xFF16A34A) else MaterialTheme.colorScheme.error
      val sign = if (isGranted) "+" else "-"
      val dateFormat = remember { SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault()) }

      Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = if (isFa) tx.descriptionFa else tx.descriptionEn,
              fontWeight = FontWeight.Medium,
              style = MaterialTheme.typography.bodySmall
            )
            Text(
              text = "${tx.transactionId} • ${dateFormat.format(Date(tx.timestamp))}",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          Text(
            text = "$sign${tx.amount}",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleMedium,
            color = color
          )
        }
      }
    }
  }
}

// -------------------------------------------------------------------------------------------------
// TAB 3: IN-APP TOKENS (Coins / Stars / Units)
// -------------------------------------------------------------------------------------------------
@Composable
private fun InAppTokensTabContent(
  coinEconomy: CoinEconomyConfig,
  isFa: Boolean,
  onSelectCoinUnitName: (String, String) -> Unit
) {
  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    item {
      Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = if (isFa) "واحد اقتصاد داخلی (Coins / Stars / Units)" else "In-App Token Economics",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = if (isFa)
              "این واحدها از اشتراک و امتیاز XP جدا هستند. نام این واحد در کل برنامه قابل تنظیم است:"
            else
              "These tokens are separate from subscriptions and XP. Unit denomination is fully configurable:",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 12.dp)
          )

          // Configurable Denomination Picker
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            val isCoins = coinEconomy.unitNameEn == "Coins"
            val isStars = coinEconomy.unitNameEn == "Stars"
            val isUnits = coinEconomy.unitNameEn == "Units"

            FilterChip(
              selected = isCoins,
              onClick = { onSelectCoinUnitName("Coins", "سکه") },
              label = { Text(if (isFa) "سکه (Coins)" else "Coins") },
              leadingIcon = { Icon(Icons.Default.Paid, contentDescription = null, modifier = Modifier.size(16.dp)) }
            )
            FilterChip(
              selected = isStars,
              onClick = { onSelectCoinUnitName("Stars", "ستاره") },
              label = { Text(if (isFa) "ستاره (Stars)" else "Stars") },
              leadingIcon = { Icon(Icons.Default.Stars, contentDescription = null, modifier = Modifier.size(16.dp)) }
            )
            FilterChip(
              selected = isUnits,
              onClick = { onSelectCoinUnitName("Units", "واحد") },
              label = { Text(if (isFa) "واحد (Units)" else "Units") },
              leadingIcon = { Icon(Icons.Default.CurrencyExchange, contentDescription = null, modifier = Modifier.size(16.dp)) }
            )
          }

          HorizontalDivider(modifier = Modifier.padding(vertical = 14.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = if (isFa) "موجودی فعلی شما:" else "Your Current Balance:",
                style = MaterialTheme.typography.labelSmall
              )
              Text(
                text = "${coinEconomy.balance} ${if (isFa) coinEconomy.unitNameFa else coinEconomy.unitNameEn}",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
              )
            }

            Surface(
              color = MaterialTheme.colorScheme.secondaryContainer,
              shape = RoundedCornerShape(8.dp)
            ) {
              Text(
                text = if (isFa) "قابل استفاده برای بسته‌ها" else "Usable for packages",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }
          }
        }
      }
    }

    item {
      Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
      ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text(
            text = if (isFa) "تفکیک دقیق سه گانه اقتصادی کارگاه توانا:" else "ABLE Forge Tri-System Architecture:",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodyMedium
          )
          Text(
            text = if (isFa)
              "۱. حق اشتراک (Subscription Entitlement): تعیین‌کننده سقف پروژه‌ها و دسترسی به ویژگی‌های پیشرفته.\n" +
                  "۲. اعتبار هوش مصنوعی (AI Credits): مصرفی و شفاف برای تحلیل‌های سنگین کد و بازنویسی ساختار.\n" +
                  "۳. واحدهای داخلی (${coinEconomy.unitNameFa}): واحد مبادله بسته و فعال‌سازی داخلی."
            else
              "1. Subscription Entitlement: Determines project limits & advanced tool unlocks.\n" +
                  "2. AI Credits: Transparent metering for heavy AST diagnostics & automated refactoring.\n" +
                  "3. In-App Tokens (${coinEconomy.unitNameEn}): Internal utility tokens for packages.",
            style = MaterialTheme.typography.bodySmall
          )
        }
      }
    }
  }
}

// -------------------------------------------------------------------------------------------------
// TAB 4: PAYMENT IDENTITY & RESTORE TAB CONTENT
// -------------------------------------------------------------------------------------------------
@Composable
private fun PaymentIdentityTabContent(
  paymentIdentity: PaymentIdentity,
  restoreMessage: String?,
  lastPurchaseResult: PurchaseResult?,
  lastVerificationResult: VerificationResult?,
  isFa: Boolean,
  onUpdateIdentity: (PaymentIdentity) -> Unit,
  onRestorePurchases: () -> Unit
) {
  var mobileInput by remember { mutableStateOf(paymentIdentity.mobile ?: "") }
  var gmailInput by remember { mutableStateOf(paymentIdentity.gmail) }
  var destRefInput by remember { mutableStateOf(paymentIdentity.paymentDestinationRef ?: "") }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // 1. Payment Identity Region Switcher
    item {
      Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
      ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
          Text(
            text = if (isFa) "پیکربندی هویت پرداخت (Payment Identity Abstraction)" else "Payment Identity Configuration",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = if (isFa)
              "تفکیک معماری ایران و خارج از کشور بر اساس الزامات فنی و امنیتی:"
            else
              "Architectural decoupling of Iran and International payment identities:",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            val isIran = paymentIdentity.type == PaymentIdentityType.IRAN_LOCAL
            FilterChip(
              selected = isIran,
              onClick = {
                onUpdateIdentity(
                  paymentIdentity.copy(type = PaymentIdentityType.IRAN_LOCAL)
                )
              },
              label = { Text(if (isFa) "ایران (موبایل + جیمیل)" else "Iran (Mobile + Gmail)") },
              modifier = Modifier.weight(1f)
            )
            FilterChip(
              selected = !isIran,
              onClick = {
                onUpdateIdentity(
                  paymentIdentity.copy(type = PaymentIdentityType.OUTSIDE_IRAN)
                )
              },
              label = { Text(if (isFa) "بین‌المللی (جیمیل + شناسه حساب)" else "International (Gmail)") },
              modifier = Modifier.weight(1f)
            )
          }

          HorizontalDivider()

          if (paymentIdentity.type == PaymentIdentityType.IRAN_LOCAL) {
            OutlinedTextField(
              value = mobileInput,
              onValueChange = {
                mobileInput = it
                onUpdateIdentity(paymentIdentity.copy(mobile = it))
              },
              label = { Text(if (isFa) "شماره تلفن همراه ایران (+98)" else "Iran Mobile Number") },
              modifier = Modifier.fillMaxWidth()
            )
          }

          OutlinedTextField(
            value = gmailInput,
            onValueChange = {
              gmailInput = it
              onUpdateIdentity(paymentIdentity.copy(gmail = it))
            },
            label = { Text(if (isFa) "حساب جیمیل کاربر (Gmail Identity)" else "Gmail Address") },
            modifier = Modifier.fillMaxWidth()
          )

          if (paymentIdentity.type == PaymentIdentityType.OUTSIDE_IRAN) {
            OutlinedTextField(
              value = destRefInput,
              onValueChange = {
                destRefInput = it
                onUpdateIdentity(paymentIdentity.copy(paymentDestinationRef = it))
              },
              label = { Text(if (isFa) "شناسه مقصد پرداخت / مرجع حساب" else "Payment Destination Identifier Ref") },
              modifier = Modifier.fillMaxWidth()
            )

            Surface(
              color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
              shape = RoundedCornerShape(8.dp)
            ) {
              Row(
                modifier = Modifier.padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                Icon(Icons.Default.Security, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Text(
                  text = if (isFa)
                    "امنیت قطعی: هیچ کلید خصوصی (Private Key) یا کلمات بازیابی (Seed Phrase) در برنامه ذخیره یا پرسیده نمی‌شود. مرجع پرداخت صرفاً یک شناسه مجرد است."
                  else
                    "Strict Security: No private keys or seed phrases are ever stored or requested. Payment destination is strictly an abstraction.",
                  style = MaterialTheme.typography.labelSmall
                )
              }
            }
          }
        }
      }
    }

    // 2. Restore Purchases
    item {
      Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
      ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Text(
            text = if (isFa) "بازیابی اشتراک و خریدها (Restore Entitlements)" else "Restore Purchases & Entitlements",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = if (isFa)
              "پس از نصب مجدد برنامه یا تعویض گوشی، می‌توانید با فشردن این دکمه وضعیت اشتراک معتبر خود را استعلام نمایید:"
            else
              "Query server-authoritative entitlements upon app reinstall or device switch:",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Button(
            onClick = onRestorePurchases,
            modifier = Modifier.testTag("restore_purchases_btn")
          ) {
            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = if (isFa) "استعلام و بازیابی اشتراک" else "Restore Entitlements")
          }

          restoreMessage?.let { msg ->
            Surface(
              color = MaterialTheme.colorScheme.surfaceVariant,
              shape = RoundedCornerShape(6.dp),
              modifier = Modifier.fillMaxWidth()
            ) {
              Text(
                text = msg,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(8.dp)
              )
            }
          }
        }
      }
    }

    // 3. Last Purchase Result
    lastPurchaseResult?.let { res ->
      item {
        Card(
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
          Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
              text = if (isFa) "آخرین نتیجه درخواست درگاه:" else "Last Gateway Request Result:",
              fontWeight = FontWeight.Bold,
              style = MaterialTheme.typography.labelMedium
            )
            Text(text = "Purchase ID: ${res.purchaseId}", style = MaterialTheme.typography.labelSmall)
            Text(text = "Status: ${res.status.name}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Text(text = "Message: ${res.rawStatusMessage}", style = MaterialTheme.typography.bodySmall)
          }
        }
      }
    }

    // 4. Last Verification Result
    lastVerificationResult?.let { ver ->
      item {
        Card(
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
          Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
              text = if (isFa) "نتیجه اعتبارسنجی سروری:" else "Server Verification Result:",
              fontWeight = FontWeight.Bold,
              style = MaterialTheme.typography.labelMedium
            )
            Text(text = "Verified: ${ver.isVerified}", fontWeight = FontWeight.Bold)
            Text(text = "Status: ${ver.status.name}", style = MaterialTheme.typography.bodySmall)
            Text(text = ver.statusMessage, style = MaterialTheme.typography.bodySmall)
          }
        }
      }
    }
  }
}
