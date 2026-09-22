package com.example.features.guidance

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DoNotDisturbOn
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.guidance.ForgeVoiceReader
import com.example.core.guidance.GuidanceSelectionMode
import com.example.core.guidance.GuidanceState
import com.example.core.guidance.GuidanceTier
import com.example.core.localization.ForgeLanguage
import com.example.features.main.ForgeSection
import com.example.ui.components.TrafficSignalType
import com.example.ui.components.UniversalTrafficPill

/**
 * 1. The Mandatory First-Launch Setup Modal:
 * "«دوست داری مقدار کمکی که از ABLE Forge میگیری را خودت انتخاب کنی یا بگذاری ABLE Forge برایت تنظیم کند؟»"
 * 1. خودم انتخاب میکنم -> Opens 4 Tiers
 * 2. تو برای من تنظیم کن -> Adaptive Assistance System
 */
@Composable
fun FirstLaunchGuidanceDialog(
  isOpen: Boolean,
  language: ForgeLanguage,
  onChooseManual: () -> Unit,
  onChooseAdaptive: () -> Unit,
  voiceReader: ForgeVoiceReader? = null
) {
  if (!isOpen) return

  val questionFa = "دوست داری مقدار کمکی که از ABLE Forge می‌گیری را خودت انتخاب کنی یا بگذاری ABLE Forge برایت تنظیم کند؟"
  val questionEn = "Would you like to choose how much help you receive from ABLE Forge, or let ABLE Forge adapt for you?"

  AlertDialog(
    onDismissRequest = { /* Modal enforces explicit choice for clarity */ },
    shape = RoundedCornerShape(16.dp),
    containerColor = MaterialTheme.colorScheme.surface,
    title = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          contentAlignment = Alignment.Center,
          modifier = Modifier
            .size(36.dp)
            .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
        ) {
          Icon(Icons.Default.School, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(
          text = if (language == ForgeLanguage.FA) "خوش‌آمدید به کارگاه توانا" else "Welcome to ABLE Forge",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold
        )
      }
    },
    text = {
      Column(modifier = Modifier.fillMaxWidth()) {
        Text(
          text = if (language == ForgeLanguage.FA) questionFa else questionEn,
          style = MaterialTheme.typography.bodyLarge,
          fontWeight = FontWeight.Medium,
          lineHeight = 24.sp,
          color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Simple language note for kids and plain-language users
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
            .padding(8.dp)
        ) {
          Text(
            text = if (language == ForgeLanguage.FA)
              "💡 نگران نباش! هر کدام را انتخاب کنی، بعداً هر موقع خواستی می‌توانی عوضش کنی."
            else
              "💡 Don't worry! You can change this setting at any time.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Multi-channel TTS Readout button
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.End
        ) {
          TextButton(
            onClick = {
              val textToRead = if (language == ForgeLanguage.FA) questionFa else questionEn
              voiceReader?.speak(textToRead)
            },
            modifier = Modifier.testTag("btn_speak_dialog")
          ) {
            Icon(Icons.Default.VolumeUp, contentDescription = "Read audio", modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(if (language == ForgeLanguage.FA) "شنیدن سؤال" else "Listen", fontSize = 12.sp)
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = onChooseAdaptive,
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("btn_choose_adaptive")
      ) {
        Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = if (language == ForgeLanguage.FA) "۲. تو برای من تنظیم کن (هوشمند و خودکار)" else "2. Let ABLE Forge Adapt For Me",
          fontWeight = FontWeight.Bold,
          fontSize = 13.sp
        )
      }
    },
    dismissButton = {
      OutlinedButton(
        onClick = onChooseManual,
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 8.dp)
          .testTag("btn_choose_manual")
      ) {
        Text(
          text = if (language == ForgeLanguage.FA) "۱. خودم انتخاب می‌کنم" else "1. I'll Choose Myself",
          fontWeight = FontWeight.Bold,
          fontSize = 13.sp
        )
      }
    }
  )
}

/**
 * 2. Manual Tier Selector Sheet / Dialog (A, B, C, D)
 */
@Composable
fun ManualTierSelectionDialog(
  isOpen: Boolean,
  currentTier: GuidanceTier,
  language: ForgeLanguage,
  onSelectTier: (GuidanceTier) -> Unit,
  onDismiss: () -> Unit
) {
  if (!isOpen) return

  AlertDialog(
    onDismissRequest = onDismiss,
    shape = RoundedCornerShape(16.dp),
    containerColor = MaterialTheme.colorScheme.surface,
    title = {
      Text(
        text = if (language == ForgeLanguage.FA) "انتخاب سطح کمک دلخواه" else "Choose Your Guidance Level",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold
      )
    },
    text = {
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        GuidanceTier.values().forEach { tier ->
          val isSelected = (currentTier == tier)
          val tierTitle = when (tier) {
            GuidanceTier.SELF -> if (language == ForgeLanguage.FA) "الف. خودم انجام می‌دهم" else "A. Self (Independent)"
            GuidanceTier.GUIDANCE -> if (language == ForgeLanguage.FA) "ب. راهنمایی مرحله‌ای" else "B. Guidance"
            GuidanceTier.TEACHING_GUIDANCE -> if (language == ForgeLanguage.FA) "ج. آموزش + راهنمایی" else "C. Teaching + Guidance"
            GuidanceTier.TEACHING_GUIDANCE_ASSISTANT -> if (language == ForgeLanguage.FA) "د. آموزش + راهنمایی + دستیار" else "D. Teaching + Guidance + Assistant"
          }
          val tierDesc = if (language == ForgeLanguage.FA) tier.faDesc else tier.enDesc

          Card(
            colors = CardDefaults.cardColors(
              containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ),
            border = BorderStroke(
              width = if (isSelected) 1.5.dp else 1.dp,
              color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
            ),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
              .fillMaxWidth()
              .clickable { onSelectTier(tier) }
              .testTag("tier_card_${tier.name.lowercase()}")
          ) {
            Row(
              modifier = Modifier.padding(10.dp),
              verticalAlignment = Alignment.Top
            ) {
              RadioButton(
                selected = isSelected,
                onClick = { onSelectTier(tier) }
              )
              Spacer(modifier = Modifier.width(6.dp))
              Column {
                Text(
                  text = tierTitle,
                  fontWeight = FontWeight.Bold,
                  style = MaterialTheme.typography.bodyMedium,
                  color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                  text = tierDesc,
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = onDismiss,
        shape = RoundedCornerShape(8.dp)
      ) {
        Text(if (language == ForgeLanguage.FA) "تأیید و شروع" else "Confirm & Start")
      }
    }
  )
}

/**
 * 3. Contextual Banner & In-Screen Guidance Card (Adaptive to the current section & active tier)
 */
@Composable
fun ContextualGuidanceBanner(
  section: ForgeSection,
  guidanceState: GuidanceState,
  language: ForgeLanguage,
  voiceReader: ForgeVoiceReader?,
  onDoNotDisturbToggle: () -> Unit,
  onRequestMoreHelp: () -> Unit,
  onOpenTutorial: () -> Unit,
  onDismissMastery: () -> Unit,
  onAcceptMastery: () -> Unit,
  modifier: Modifier = Modifier
) {
  if (guidanceState.doNotDisturb) {
    // Subtle, unobtrusive chip for professional/distraction-free mode
    Row(
      modifier = modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp, vertical = 4.dp),
      horizontalArrangement = Arrangement.End
    ) {
      TextButton(
        onClick = onDoNotDisturbToggle,
        modifier = Modifier.testTag("btn_resume_guidance")
      ) {
        Icon(Icons.Default.DoNotDisturbOn, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.outline)
        Spacer(modifier = Modifier.width(4.dp))
        Text(
          text = if (language == ForgeLanguage.FA) "حالت بی‌صدا فعال است (روشن کردن راهنما)" else "Do Not Disturb active (Tap to resume)",
          fontSize = 11.sp,
          color = MaterialTheme.colorScheme.outline
        )
      }
    }
    return
  }

  // If tier is SELF, do not show automatic banners
  if (guidanceState.currentTier == GuidanceTier.SELF && !guidanceState.masterySuggestionVisible) {
    return
  }

  // Mastery suggestion alert:
  // «به نظر می‌رسد این کار را خوب یاد گرفته‌ای. دوست داری این بار خودت انجامش بدهی؟»
  if (guidanceState.masterySuggestionVisible) {
    Card(
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
      shape = RoundedCornerShape(12.dp),
      border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary),
      modifier = modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp, vertical = 6.dp)
        .testTag("card_mastery_suggestion")
    ) {
      Column(modifier = Modifier.padding(12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = if (language == ForgeLanguage.FA) "پیشنهاد دوستانه توانا" else "Friendly Suggestion",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleSmall
          )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
          text = if (language == ForgeLanguage.FA)
            "به نظر می‌رسد این کار را خوب یاد گرفته‌ای. دوست داری این بار خودت انجامش بدهی؟"
          else
            "Looks like you've mastered this! Would you like to do it on your own this time?",
          style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.End
        ) {
          TextButton(onClick = onDismissMastery) {
            Text(if (language == ForgeLanguage.FA) "نه، هنوز کمک می‌خواهم" else "Keep helping me")
          }
          Spacer(modifier = Modifier.width(8.dp))
          Button(
            onClick = onAcceptMastery,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
          ) {
            Text(if (language == ForgeLanguage.FA) "بله، خودم انجام می‌دهم" else "Yes, I'll do it")
          }
        }
      }
    }
    return
  }

  // Section-specific ultra-simple kid/beginner explanations
  val (stepTip, detailedExplain, actionChip) = getSectionGuidanceContent(section, language)

  Card(
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)),
    shape = RoundedCornerShape(12.dp),
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 12.dp, vertical = 6.dp)
      .testTag("contextual_guidance_card")
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
      ) {
        Icon(Icons.Default.Lightbulb, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = stepTip,
          fontWeight = FontWeight.Bold,
          style = MaterialTheme.typography.bodyMedium,
          modifier = Modifier.weight(1f)
        )
        // Speak button
        IconButton(
          onClick = { voiceReader?.speak("$stepTip. $detailedExplain") },
          modifier = Modifier.size(32.dp)
        ) {
          Icon(Icons.Default.VolumeUp, contentDescription = "Listen to step hint", modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.primary)
        }
      }

      // If level includes TEACHING or ASSISTANT, provide plain child-friendly explanation
      if (guidanceState.currentTier == GuidanceTier.TEACHING_GUIDANCE || guidanceState.currentTier == GuidanceTier.TEACHING_GUIDANCE_ASSISTANT) {
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = detailedExplain,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          lineHeight = 18.sp
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Bottom control bar with:
      // 1. «فعلاً مزاحم نشو» (Do not disturb)
      // 2. Full Academy link
      // 3. Current Tier badge
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        TextButton(
          onClick = onDoNotDisturbToggle,
          modifier = Modifier.testTag("btn_dnd")
        ) {
          Icon(Icons.Default.DoNotDisturbOn, contentDescription = null, modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text(if (language == ForgeLanguage.FA) "فعلاً مزاحم نشو" else "Don't disturb for now", fontSize = 11.sp)
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          TextButton(onClick = onOpenTutorial) {
            Text(if (language == ForgeLanguage.FA) "آموزش کامل 📖" else "Full Academy 📖", fontSize = 11.sp)
          }

          Spacer(modifier = Modifier.width(4.dp))

          UniversalTrafficPill(
            signal = TrafficSignalType.READY,
            label = when (guidanceState.currentTier) {
              GuidanceTier.SELF -> "SELF"
              GuidanceTier.GUIDANCE -> "GUIDE"
              GuidanceTier.TEACHING_GUIDANCE -> "TEACH"
              GuidanceTier.TEACHING_GUIDANCE_ASSISTANT -> "ASSIST"
            }
          )
        }
      }
    }
  }
}

private fun getSectionGuidanceContent(
  section: ForgeSection,
  language: ForgeLanguage
): Triple<String, String, String> {
  val isFa = (language == ForgeLanguage.FA)
  return when (section) {
    ForgeSection.PROJECTS -> Triple(
      if (isFa) "مرحله ۱: انتخاب یا ساخت پروژه" else "Step 1: Choose or Create Project",
      if (isFa) "روی دکمه «+» بزن تا یک پروژه وب، کاتلین یا متنی بسازی. هر پروژه در حافظه گوشی خودت ذخیره می‌شود." else "Tap '+' to create a new project. Everything is saved safely in device storage.",
      if (isFa) "ساخت پروژه" else "New Project"
    )
    ForgeSection.FILES -> Triple(
      if (isFa) "مرحله ۲: انتخاب فایل برای نوشتن کد" else "Step 2: Pick a File to Edit",
      if (isFa) "با انگشت روی هر فایل بزن تا در ویرایشگر باز شود، یا با دکمه بالا یک فایل جدید اضافه کن." else "Tap any file to open it in the editor, or tap '+' to create a new file.",
      if (isFa) "باز کردن فایل" else "Open File"
    )
    ForgeSection.EDITOR -> Triple(
      if (isFa) "مرحله ۳: کدنویسی آسان با نوار علامت‌ها" else "Step 3: Easy Code Editing with Symbols",
      if (isFa) "کد خودت را بنویس. از علامت‌های بالای کیبورد مثل { } و = استفاده کن و در پایان دکمه ذخیره 💾 را بزن." else "Write code comfortably. Use the mobile symbol toolbar for brackets and tap Save.",
      if (isFa) "ذخیره فایل" else "Save File"
    )
    ForgeSection.CONSOLE -> Triple(
      if (isFa) "مرحله ۴: تست دستورها با دکمه‌های آماده" else "Step 4: Run Commands with Fast Chips",
      if (isFa) "روی help یا ls یا stat ضربه بزن تا نتیجه فایل‌ها و اجرای کد را در ترمینال ببینی." else "Tap chips like 'help' or 'stat' to run actions instantly without typing.",
      if (isFa) "اجرای help" else "Run Help"
    )
    ForgeSection.GIT -> Triple(
      if (isFa) "مرحله ۵: ثبت تغییرات مثل عکس در بازی" else "Step 5: Record Checkpoint in Git",
      if (isFa) "یک جمله کوتاه بنویس (مثلاً: دکمه اضافه شد) و دکمه «ثبت کامیت» را بزن تا همیشه بتوانی به این مرحله برگردی." else "Enter a short commit note and tap Commit Changes to record your milestone.",
      if (isFa) "ثبت کامیت" else "Commit"
    )
    ForgeSection.AI_FIX -> Triple(
      if (isFa) "عیب‌یابی: بررسی پرانتزها و اشتباهات تایپی" else "Diagnostics: Syntax & Typo Fixer",
      if (isFa) "دکمه «بررسی کد» را بزن تا برنامه همه خطوط را بخواند و اگر چیزی جا افتاده با رنگ زرد یا قرمز نشان دهد." else "Tap Run Diagnostics to detect missing brackets or typos right on device.",
      if (isFa) "بررسی کد" else "Check Code"
    )
    ForgeSection.DEPLOY -> Triple(
      if (isFa) "مرحله ۶: دیدن نتیجه کار به صورت زنده" else "Step 6: Live Preview & Export",
      if (isFa) "صفحه ساخته شده‌ات را اینجا ببین و دکمه‌هایش را امتحان کن! همچنین می‌توانی فایل zip را دریافت کنی." else "Interact with your live preview directly or export a zip bundle.",
      if (isFa) "مشاهده زنده" else "Live Preview"
    )
    ForgeSection.COMMUNITY -> Triple(
      if (isFa) "پیش‌نمایش جامعه سازندگان" else "Community Preview",
      if (isFa) "این بخش کاملاً مستقل است و به هیچ وجه کار آفلاین شما را کند یا مختل نمی‌کند." else "Fully decoupled module that never disrupts core offline software creation.",
      if (isFa) "دیدن نمونه‌ها" else "Browse"
    )
    ForgeSection.TUTORIAL -> Triple(
      if (isFa) "آموزشگاه توانا: ۱۲ درس عملی و ساده" else "ABLE Academy: 12 Practical Lessons",
      if (isFa) "از شروع از صفر تا استقرار و دسترسی‌پذیری، با زبان ساده و خواندن صوتی همه چیز را یاد بگیر." else "Learn step-by-step from ground zero with audio narration and practical tasks.",
      if (isFa) "انتخاب درس" else "Select Lesson"
    )
    ForgeSection.HELP -> Triple(
      if (isFa) "راهنمای علائم و نشانه‌های ترافیکی" else "Universal Traffic Signs Guide",
      if (isFa) "دایره سبز یعنی آماده، لوزی زرد یعنی تغییر یافته، هشت‌ضلعی قرمز یعنی خطا، مربع خاکستری یعنی آفلاین." else "Dual-encoded cues ensure no status relies solely on color: shapes and labels guide every step.",
      if (isFa) "مشاهده علائم" else "View Signs"
    )
    ForgeSection.SETTINGS -> Triple(
      if (isFa) "تنظیمات دسترسی‌پذیری و سطح کمک" else "Accessibility & Guidance Settings",
      if (isFa) "درشت‌نمایی متن، حالت کم‌حرکت (Reduced Motion)، رنگ پرکنتراست و تغییر سطح راهنمایی." else "Configure text size, high contrast, reduced motion, and guidance levels.",
      if (isFa) "تنظیمات" else "Settings"
    )
  }
}
