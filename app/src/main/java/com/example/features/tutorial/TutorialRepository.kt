package com.example.features.tutorial

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessibilityNew
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CallSplit
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.features.main.ForgeSection

data class TutorialChapter(
  val id: String,
  val titleFa: String,
  val titleEn: String,
  val shortDescFa: String,
  val shortDescEn: String,
  val contentFa: String,
  val contentEn: String,
  val simpleExplainFa: String, // زبان بسیار روان و قابل فهم برای نوآموزان
  val actionTarget: ForgeSection,
  val actionLabelFa: String,
  val actionLabelEn: String,
  val icon: ImageVector
)

object TutorialRepository {
  val chapters = listOf(
    TutorialChapter(
      id = "start_from_zero",
      titleFa = "۱. شروع از صفر (ساخت اولین کار)",
      titleEn = "1. Start From Zero",
      shortDescFa = "چطور بدون ترس اولین برنامه خود را بسازید.",
      shortDescEn = "How to build your very first app without fear.",
      contentFa = "برنامه‌نویسی مثل چیدن لگو یا نقاشی کشیدن است! در ABLE Forge نیاز نیست کدهای سخت حفظ کنی. همه چیز از یک «پروژه» شروع می‌شود. پروژه مثل یک دفترچه است که نقاشی‌هایت را در آن می‌گذاری.",
      contentEn = "Programming is like building with Lego blocks! In ABLE Forge you don't need to memorize difficult syntax. Everything starts with a Project.",
      simpleExplainFa = "💡 به زبان ساده: روی علامت مثبت (+) بزن تا یک دفترچه جدید بسازی!",
      actionTarget = ForgeSection.PROJECTS,
      actionLabelFa = "برو به بخش پروژه‌ها",
      actionLabelEn = "Go to Projects",
      icon = Icons.Default.Build
    ),
    TutorialChapter(
      id = "create_project",
      titleFa = "۲. ساخت پروژه جدید",
      titleEn = "2. Project Templates",
      shortDescFa = "انتخاب قالب‌های آماده وب، کاتلین یا متنی.",
      shortDescEn = "Choose ready-made templates for Web, Kotlin, or Docs.",
      contentFa = "وقتی پروژه می‌سازی، چند قالب آماده داری: برنامه وب (صفحه سایت)، اسکریپت کاتلین (ماشین حساب و بازی)، و دفترچه مستندات. هر کدام را انتخاب کنی، خودش فایل‌های اولیه را برایت می‌چیند.",
      contentEn = "Choose from ready-made templates: Web App (HTML), Kotlin Script (Calculators & Logic), or Docs.",
      simpleExplainFa = "💡 به زبان ساده: قالب «برنامه وب» را انتخاب کن تا با یک دکمه نتیجه را روی صفحه ببینی.",
      actionTarget = ForgeSection.PROJECTS,
      actionLabelFa = "ساخت یا تغییر پروژه",
      actionLabelEn = "Manage Projects",
      icon = Icons.Default.Folder
    ),
    TutorialChapter(
      id = "files",
      titleFa = "۳. فایل‌ها و اتاق نگهداری کد",
      titleEn = "3. Files & Workspace",
      shortDescFa = "دیدن برگه و فایل‌های ساخته شده در گوشی.",
      shortDescEn = "Inspect and organize all files saved in local device storage.",
      contentFa = "هر پروژه مثل یک پوشه است که چند فایل مثل index.html و style.css دارد. همه فایل‌ها داخل گوشی خودت ذخیره می‌شوند و اگر اینترنت هم قطع باشد، هیچ چیز پاک نمی‌شود.",
      contentEn = "Each project stores its files safely in device storage. Fully offline, no data loss.",
      simpleExplainFa = "💡 به زبان ساده: با انگشت روی اسم هر فایل بزن تا صفحه‌اش برای نوشتن باز شود.",
      actionTarget = ForgeSection.FILES,
      actionLabelFa = "مشاهده فایل‌های من",
      actionLabelEn = "View Files",
      icon = Icons.Default.Folder
    ),
    TutorialChapter(
      id = "editor",
      titleFa = "۴. ویرایشگر (کد نوشتن با انگشت)",
      titleEn = "4. Code Editor & Symbol Bar",
      shortDescFa = "نوشتن علامت‌ها مثل { } [ ] بدون دردسر کیبورد.",
      shortDescEn = "Write code easily on touch screens using the Symbol Toolbar.",
      contentFa = "تایپ کردن علامت‌هایی مثل آکولاد { } و پرانتز ( ) روی کیبورد گوشی سخت است. برای همین بالای صفحه یک نوار مخصوص دکمه‌های پرکاربرد گذاشتیم تا فقط با یک لمس آن‌ها را درج کنی!",
      contentEn = "Typing programming symbols on mobile is difficult. Use the symbol toolbar to insert brackets and symbols with one tap.",
      simpleExplainFa = "💡 به زبان ساده: کد بنویس، دکمه ذخیره رو بزن و اگر علامت خواستی از نوار بالای کیبورد انتخاب کن.",
      actionTarget = ForgeSection.EDITOR,
      actionLabelFa = "ورود به ویرایشگر",
      actionLabelEn = "Open Editor",
      icon = Icons.Default.Code
    ),
    TutorialChapter(
      id = "git",
      titleFa = "۵. گیت و ماشین زمان (ثبت تغییرات)",
      titleEn = "5. Git & Time Machine",
      shortDescFa = "اگر کدت خراب شد، چطور با خیال راحت به عقب برگردی.",
      shortDescEn = "Save milestones so you can safely restore your work anytime.",
      contentFa = "گیت مثل عکس گرفتن از پیشرفت بازی است! هر وقت یک کار خوب کردی، یک پیام کوتاه می‌نویسی و دکمه «ثبت کامیت» را می‌زنی. اینطوری هیچ‌وقت کارت از دست نمی‌رود.",
      contentEn = "Git is like saving your game checkpoint. Whenever you make progress, record a commit message.",
      simpleExplainFa = "💡 به زبان ساده: یک یادداشت بنویس (مثلاً دکمه اضافه شد) و دکمه ثبت را بزن.",
      actionTarget = ForgeSection.GIT,
      actionLabelFa = "دیدن گیت و کامیت‌ها",
      actionLabelEn = "Open Git",
      icon = Icons.Default.CallSplit
    ),
    TutorialChapter(
      id = "aifix",
      titleFa = "۶. رفع خطای خودکار (AI Fix)",
      titleEn = "6. AI & Syntax Fixer",
      shortDescFa = "پیدا کردن اشتباهات تایپی و بستن پرانتزهای باز مانده.",
      shortDescEn = "Detect typos, unclosed brackets, and syntax issues.",
      contentFa = "اگر یادت رفت پرانتزی را ببندی، سیستم بررسی هوشمند بهت با رنگ زرد یا قرمز علامت می‌دهد و می‌گوید دقیقاً در کدام خط باید چه کار کنی. نیازی به نگرانی نیست.",
      contentEn = "Automatic local diagnostics highlight syntax errors and unclosed tags with clear guidance.",
      simpleExplainFa = "💡 به زبان ساده: دکمه بررسی را بزن تا برنامه عیب‌ها را خودش پیدا کند.",
      actionTarget = ForgeSection.AI_FIX,
      actionLabelFa = "بررسی و رفع خطا",
      actionLabelEn = "Check Code",
      icon = Icons.Default.AutoAwesome
    ),
    TutorialChapter(
      id = "console",
      titleFa = "۷. کنسول فرمان (دستورهای آماده)",
      titleEn = "7. Command Console",
      shortDescFa = "دستور دادن سریع با زدن روی دکمه‌های آماده.",
      shortDescEn = "Run commands quickly with pre-built chips like help, ls, and stat.",
      contentFa = "در کنسول لازم نیست دستورهای سخت تایپ کنی. دکمه‌های آماده مثل help و ls و stat وجود دارد تا با یک لمس، وضعیت فایل‌ها و خروجی کد را ببینی.",
      contentEn = "No need to memorize terminal commands. Tap built-in command chips to see results immediately.",
      simpleExplainFa = "💡 به زبان ساده: روی دکمه help بزن تا همه دستورها مثل فهرست بازی نشان داده شوند.",
      actionTarget = ForgeSection.CONSOLE,
      actionLabelFa = "باز کردن کنسول",
      actionLabelEn = "Open Console",
      icon = Icons.Default.PlayArrow
    ),
    TutorialChapter(
      id = "deploy",
      titleFa = "۸. استقرار و دیدن نتیجه (Deploy)",
      titleEn = "8. Deployment & Live Preview",
      shortDescFa = "دیدن زنده برنامه ساخته شده مثل یک اپ واقعی.",
      shortDescEn = "Preview your application live directly inside the app.",
      contentFa = "اینجا هیجان‌انگیزترین قسمت است! می‌توانی صفحه وب یا برنامه‌ات را به صورت زنده ببینی و با دکمه‌هایش کار کنی، یا فایل zip آن را ذخیره کنی.",
      contentEn = "See your creation live! Test buttons and interactions in the built-in live preview.",
      simpleExplainFa = "💡 به زبان ساده: ببین چیزی که ساختی چطور مثل یک وب‌سایت واقعی کار می‌کند!",
      actionTarget = ForgeSection.DEPLOY,
      actionLabelFa = "دیدن پیش‌نمایش زنده",
      actionLabelEn = "Live Preview",
      icon = Icons.Default.RocketLaunch
    ),
    TutorialChapter(
      id = "offline_mode",
      titleFa = "۹. حالت آفلاین و امنیت کامل",
      titleEn = "9. Offline Mode & Safety",
      shortDescFa = "کار در هواپیما یا بدون اینترنت با امنیت صد درصد.",
      shortDescEn = "Works everywhere without internet, completely secure.",
      contentFa = "این برنامه هیچ نیازی به اینترنت دائمی ندارد. همه پروژه‌ها در حافظه محلی گوشی در دیتابیس امن Room نگهداری می‌شوند و هیچ اطلاعاتی بدون اجازه شما به جایی فرستاده نمی‌شود.",
      contentEn = "Fully offline-first. All data is stored in the local SQLite/Room database.",
      simpleExplainFa = "💡 به زبان ساده: اینترنت نداری؟ هیچ مشکلی نیست، با خیال راحت کدت را بنویس!",
      actionTarget = ForgeSection.PROJECTS,
      actionLabelFa = "بررسی پروژه‌ها",
      actionLabelEn = "Check Projects",
      icon = Icons.Default.WifiOff
    ),
    TutorialChapter(
      id = "accessibility",
      titleFa = "۱۰. دسترسی‌پذیری و تمرکز ویژه ADHD",
      titleEn = "10. Accessibility & ADHD Focus",
      shortDescFa = "کاهش حواس‌پرتی، دکمه‌های بزرگ و رنگ‌های پرکنتراست.",
      shortDescEn = "Reduced motion, high contrast, and large comfortable touch targets.",
      contentFa = "برای کسانی که لرزش دست دارند دکمه‌ها حداقل ۴۸ پیکسل هستند. برای کاهش حواس‌پرتی، انیمیشن‌های غیرضروری با حالت Reduced Motion خاموش می‌شوند و نشانه‌های چراغ راهنما با دو نشانه شکل و متن همه چیز را توضیح می‌دهند.",
      contentEn = "Engineered for accessibility: 48dp touch targets, dual-encoded visual cues, and reduced motion.",
      simpleExplainFa = "💡 به زبان ساده: در تنظیمات می‌توانی فونت را درشت کنی و رنگ‌های پررنگ را فعال کنی.",
      actionTarget = ForgeSection.SETTINGS,
      actionLabelFa = "تنظیمات دسترسی‌پذیری",
      actionLabelEn = "Open Settings",
      icon = Icons.Default.AccessibilityNew
    ),
    TutorialChapter(
      id = "stt_tts",
      titleFa = "۱۱. خواندن صوتی و گفتار (STT / TTS)",
      titleEn = "11. Voice Readout & Audio Assistance",
      shortDescFa = "شنیدن راهنمایی‌ها با صدای بلند برای راحتی چشم.",
      shortDescEn = "Listen to guidance explanations with spoken audio.",
      contentFa = "اگر خواندن متن طولانی چشم‌هایت را خسته می‌کند، می‌توانی دکمه بلندگو 🔊 را بزنی تا برنامه همه راهنمایی‌ها و مراحل را آرام و شمرده برایت بخواند.",
      contentEn = "Listen to any instruction or explanation with the speech audio button.",
      simpleExplainFa = "💡 به زبان ساده: روی دکمه بلندگو بزن تا برنامه برایت متن را بخواند.",
      actionTarget = ForgeSection.TUTORIAL,
      actionLabelFa = "شنیدن راهنما",
      actionLabelEn = "Audio Readout",
      icon = Icons.Default.Mic
    ),
    TutorialChapter(
      id = "community",
      titleFa = "۱۲. جامعه و نمونه‌های دیگران",
      titleEn = "12. Community & Sample Projects",
      shortDescFa = "دیدن کدهای آماده برای ایده گرفتن بدون پیچیدگی.",
      shortDescEn = "Explore standalone decoupled preview templates and community ideas.",
      contentFa = "بخش جامعه کاملاً جداگانه طراحی شده تا حتی اگر هیچ اتصالی نداشته باشی، مزاحم برنامه‌نویسی روزمره‌ات نشود و فقط در زمان دلخواهت نمونه‌های جدید ببینی.",
      contentEn = "Decoupled preview module that never blocks offline core development.",
      simpleExplainFa = "💡 به زبان ساده: کارهای جالب دیگران را ببین و برای کارهای خودت ایده بگیر.",
      actionTarget = ForgeSection.COMMUNITY,
      actionLabelFa = "مشاهده جامعه",
      actionLabelEn = "Explore Community",
      icon = Icons.Default.Group
    )
  )
}
