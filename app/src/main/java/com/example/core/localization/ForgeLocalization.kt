package com.example.core.localization

import androidx.compose.ui.unit.LayoutDirection

enum class ForgeLanguage(
  val code: String,
  val nativeName: String,
  val englishName: String,
  val layoutDirection: LayoutDirection
) {
  FA("fa", "فارسی", "Persian", LayoutDirection.Rtl),
  EN("en", "English", "English", LayoutDirection.Ltr),
  AR("ar", "العربية", "Arabic", LayoutDirection.Rtl),
  ES("es", "Español", "Spanish", LayoutDirection.Ltr),
  ZH("zh", "中文 (Preview)", "Chinese", LayoutDirection.Ltr),
  RU("ru", "Русский (Preview)", "Russian", LayoutDirection.Ltr)
}

object ForgeStrings {
  fun get(key: String, lang: ForgeLanguage): String {
    val dict = when (lang) {
      ForgeLanguage.FA -> faStrings
      ForgeLanguage.AR -> arStrings
      ForgeLanguage.ES -> esStrings
      ForgeLanguage.ZH -> zhStrings
      ForgeLanguage.RU -> ruStrings
      ForgeLanguage.EN -> enStrings
    }
    return dict[key] ?: enStrings[key] ?: key
  }

  private val enStrings = mapOf(
    "app_title" to "ABLE Forge",
    "app_subtitle" to "Mobile Software Engineering Forge",
    "slogan" to "Build. Fix. Create. Anywhere.",

    // Nav
    "nav_projects" to "Projects",
    "nav_files" to "Files",
    "nav_editor" to "Editor",
    "nav_git" to "Git",
    "nav_console" to "Console",
    "nav_aifix" to "AI Fix",
    "nav_deploy" to "Deploy",
    "nav_community" to "Community",
    "nav_monetization" to "Plans & AI Credits",
    "nav_tutorial" to "Tutorial",
    "nav_help" to "Visual Guide",
    "nav_settings" to "Settings",

    // Dual statuses
    "status_ready" to "READY",
    "status_modified" to "MODIFIED",
    "status_issue" to "ISSUE FOUND",
    "status_unconfigured" to "NOT CONFIGURED",
    "status_clean" to "CLEAN TREE",

    // Traffic Flow Steps
    "step_1_create" to "1. Setup Project",
    "step_2_code" to "2. Edit Code",
    "step_3_verify" to "3. Test & Fix",
    "step_4_commit" to "4. Git Stage",
    "step_5_deploy" to "5. Deploy",

    // Common Actions
    "action_new_project" to "New Project",
    "action_new_file" to "New File",
    "action_save" to "Save File",
    "action_run" to "Execute",
    "action_commit" to "Commit Changes",
    "action_cancel" to "Cancel",
    "action_delete" to "Delete",
    "action_clear" to "Clear",
    "action_refresh" to "Refresh",
    "action_undo" to "Undo",
    "action_redo" to "Redo",

    // Projects Screen
    "projects_title" to "Workspaces & Projects",
    "projects_desc" to "Real offline projects stored locally on device.",
    "projects_empty" to "No projects yet. Tap '+' to create your first mobile workspace.",
    "project_name_hint" to "Project Name (e.g., mini-calc)",
    "project_template_web" to "Static Web App (HTML/CSS/JS)",
    "project_template_kotlin" to "Kotlin Script (Executable logic)",
    "project_template_api" to "JSON REST Spec & Mock API",
    "project_template_markdown" to "Engineering Documentation",
    "project_active" to "Active Project",
    "project_switch" to "Open",

    // Files Screen
    "files_title" to "Project Files",
    "files_empty" to "No files in this project.",
    "file_create_hint" to "filename.ext",
    "file_delete_confirm" to "Are you sure you want to delete this file?",

    // Editor Screen
    "editor_title" to "Mobile Code Forge",
    "editor_saved" to "Saved to local storage",
    "editor_unsaved" to "Unsaved changes",
    "editor_symbol_bar" to "Quick Symbols for Mobile Keyboard",
    "editor_no_file" to "Select or create a file to start editing.",

    // Git Screen
    "git_title" to "Local Git Control",
    "git_subtitle" to "Real local commit history and repository status.",
    "git_branch" to "Active Branch",
    "git_commit_msg" to "Commit message...",
    "git_staged_files" to "Modified / Staged Files",
    "git_no_changes" to "Working tree clean. No uncommitted modifications.",
    "git_remote_status" to "Remote Sync: Offline / Local Only (Configure remote URL to enable push/pull)",
    "git_history" to "Commit History",
    "git_clone" to "Clone Repository",
    "git_pull" to "Git Pull",
    "git_push" to "Git Push",
    "git_add" to "Git Add",
    "git_concurrency_error" to "Concurrent Modification Error: Remote HEAD has diverged. Auto-retry disabled.",

    // Console Screen
    "console_title" to "Development Command Console",
    "console_subtitle" to "Safe, restricted development console. Arbitrary shell execution is disabled.",
    "console_hint" to "Enter safe command (pwd, ls, cat, stat, echo, git status, git log, clear)...",
    "console_welcome" to "Development Command Console v0.1\nRestricted safe execution environment. Type 'help' for available commands.",

    // AI Fix Screen
    "aifix_title" to "AI Code Fixer & Diagnostics",
    "aifix_subtitle" to "Human-reviewed Crash-Recoverable Replacement pipeline.",
    "aifix_run_check" to "Run Code Diagnostics",
    "aifix_local_lint" to "Diagnostic Issues",
    "aifix_proposal_title" to "Fix Proposal (Requires Review)",
    "aifix_apply_btn" to "Review & Apply Fix",
    "aifix_verify_badge" to "Crash-Recoverable Replacement",
    "aifix_gemini_status" to "AI Provider Status",
    "aifix_gemini_info" to "Real API: Set GEMINI_API_KEY in AI Studio Secrets panel. No simulated successes are returned.",
    "aifix_no_issues" to "All local syntax checks passed! Code is well-formed.",

    // Deploy Screen
    "deploy_title" to "Deployment & Preview",
    "deploy_subtitle" to "Real local bundling & execution pipeline.",
    "deploy_target_preview" to "Local In-App Webview Preview",
    "deploy_target_bundle" to "Export Static Web Bundle (ZIP)",
    "deploy_target_spec" to "Android APK Manifest Spec",
    "deploy_status_note" to "Deployment pipeline verified locally on mobile storage.",

    // Community & Economy
    "community_title" to "ABLE Community & Economy",
    "community_subtitle" to "Modular collaboration network. Core development remains strictly local & independent.",
    "community_notice" to "Decoupled Architecture: Core IDE operations run 100% offline without remote network dependency.",
    "community_profile" to "Developer Profile",
    "community_rooms" to "Topic Rooms",
    "community_collab" to "Collaboration & Roles",
    "community_xp_ledger" to "XP Ledger & Economy",
    "community_leagues" to "Leagues & Recognition",
    "community_disclaimer" to "XP points or purchases DO NOT guarantee that an individual receives land, employment, a management position, or a council seat.",

    // Tutorial
    "tutorial_title" to "Mobile Dev Tutorial",
    "tutorial_subtitle" to "Visual guide to developing software on your mobile device.",
    "tutorial_step1_title" to "1. Forge Your Project",
    "tutorial_step1_desc" to "Pick a template or start blank. Projects are saved persistently in device storage.",
    "tutorial_step2_title" to "2. Code with Mobile Precision",
    "tutorial_step2_desc" to "Use the dedicated symbol toolbar to insert brackets and operators without keyboard switching.",
    "tutorial_step3_title" to "3. Test Locally in Console",
    "tutorial_step3_desc" to "Run commands, test outputs, and inspect file trees using the built-in mobile shell.",
    "tutorial_step4_title" to "4. Stage & Commit",
    "tutorial_step4_desc" to "Track your progress with real local Git commits and clean message logs.",

    // Help
    "help_title" to "Visual Guidance & Signs",
    "help_subtitle" to "Traffic signs and visual cues explained.",
    "help_traffic_green" to "Green Circle: Everything is ready and clean.",
    "help_traffic_amber" to "Amber Diamond: Unsaved edits or modified files exist.",
    "help_traffic_red" to "Red Octagon: Syntax error or missing configuration.",
    "help_traffic_gray" to "Gray Square: Service offline or not yet configured.",

    // Settings
    "settings_title" to "Forge Settings",
    "settings_theme" to "Theme Mode",
    "theme_dark" to "Dark Slate",
    "theme_light" to "Light Titanium",
    "theme_high_contrast" to "High Contrast (Accessibility)",
    "settings_language" to "Interface Language",
    "settings_font_scale" to "Text Size Scale",
    "settings_reduced_motion" to "Reduced Motion (Accessibility)",
    "settings_reduced_motion_desc" to "Disables non-essential animations and transitions.",
    "settings_reset_demo" to "Load Sample Projects",
    "settings_reset_done" to "Sample project reloaded."
  )

  private val faStrings = mapOf(
    "app_title" to "ABLE Forge",
    "app_subtitle" to "کارگاه توانا — مهندسی نرم‌افزار روی موبایل",
    "slogan" to "بساز. رفع خطا کن. خلق کن. هر کجا.",

    // Nav
    "nav_projects" to "پروژه‌ها",
    "nav_files" to "فایل‌ها",
    "nav_editor" to "ویرایشگر",
    "nav_git" to "گیت",
    "nav_console" to "کنسول",
    "nav_aifix" to "رفع خطا (AI)",
    "nav_deploy" to "استقرار",
    "nav_community" to "جامعه",
    "nav_monetization" to "پلن‌ها و اعتبارات",
    "nav_tutorial" to "آموزش",
    "nav_help" to "راهنمای تصویری",
    "nav_settings" to "تنظیمات",

    // Dual statuses
    "status_ready" to "آماده",
    "status_modified" to "تغییریافته",
    "status_issue" to "خطا / نیاز به بررسی",
    "status_unconfigured" to "تنظیم‌نشده",
    "status_clean" to "درخت بدون تغییر",

    // Traffic Flow Steps
    "step_1_create" to "۱. ایجاد پروژه",
    "step_2_code" to "۲. نوشتن کد",
    "step_3_verify" to "۳. تست و عیب‌یابی",
    "step_4_commit" to "۴. ثبت در گیت",
    "step_5_deploy" to "۵. استقرار و خروجی",

    // Actions
    "action_new_project" to "پروژه جدید",
    "action_new_file" to "فایل جدید",
    "action_save" to "ذخیره فایل",
    "action_run" to "اجرا",
    "action_commit" to "ثبت کامیت",
    "action_cancel" to "انصراف",
    "action_delete" to "حذف",
    "action_clear" to "پاک‌سازی",
    "action_refresh" to "تازه‌سازی",
    "action_undo" to "بازگردانی",
    "action_redo" to "تکرار",

    // Projects
    "projects_title" to "محیط‌های کاری و پروژه‌ها",
    "projects_desc" to "پروژه‌های واقعی ذخیره شده به صورت محلی در حافظه گوشی.",
    "projects_empty" to "هنوز پروژه‌ای ثبت نشده. برای ساخت اولین پروژه روی «+» ضربه بزنید.",
    "project_name_hint" to "نام پروژه (مثلاً: mini-calc)",
    "project_template_web" to "برنامه وب ایستا (HTML/CSS/JS)",
    "project_template_kotlin" to "اسکریپت کاتلین (منطق اجرایی)",
    "project_template_api" to "مشخصات JSON REST و ماک",
    "project_template_markdown" to "مستندات مهندسی",
    "project_active" to "پروژه فعال",
    "project_switch" to "ورود به پروژه",

    // Files
    "files_title" to "فایل‌های پروژه",
    "files_empty" to "فایلی در این پروژه وجود ندارد.",
    "file_create_hint" to "نام‌فایل با پسوند (مثلاً index.html)",
    "file_delete_confirm" to "آیا از حذف این فایل مطمئن هستید؟",

    // Editor
    "editor_title" to "ویرایشگر همراه توانا",
    "editor_saved" to "در حافظه محلی ذخیره شد",
    "editor_unsaved" to "تغییرات ذخیره‌نشده",
    "editor_symbol_bar" to "نوار نمادهای برنامه‌نویسی ویژه موبایل",
    "editor_no_file" to "یک فایل را برای شروع ویرایش انتخاب یا ایجاد کنید.",

    // Git
    "git_title" to "کنترل نسخه محلی Git",
    "git_subtitle" to "تاریخچه کامیت‌ها و وضعیت واقعی مخزن در حافظه دستگاه.",
    "git_branch" to "شاخه فعال",
    "git_commit_msg" to "پیام کامیت (توضیح تغییرات)...",
    "git_staged_files" to "فایل‌های تغییر یافته",
    "git_no_changes" to "مخزن تمیز است؛ تغییری برای کامیت وجود ندارد.",
    "git_remote_status" to "وضعیت سرور دوردست: آفلاین / فقط محلی (برای Push/Pull نشانی Remote تنظیم شود)",
    "git_history" to "تاریخچه کامیت‌ها",
    "git_clone" to "کلون مخزن گیت",
    "git_pull" to "دریافت تغییرات (Git Pull)",
    "git_push" to "ارسال تغییرات (Git Push)",
    "git_add" to "افزودن به استیج (Git Add)",
    "git_concurrency_error" to "خطای تداخل همزمانی: شاخه سرور تغییر کرده است. تلاش مجدد خودکار در نسخه v0.1 غیرفعال است.",

    // Console
    "console_title" to "Development Command Console",
    "console_subtitle" to "کنسول خط فرمان کنترل‌شده و امن. اجرای شل دلخواه غیرفعال است.",
    "console_hint" to "دستور امن را وارد کنید (pwd, ls, cat, stat, echo, git status, git log, clear)...",
    "console_welcome" to "کنسول دستورات توسعه (Development Command Console v0.1)\nمحیط اجرای امن و ایزوله. برای مشاهده دستورات 'help' را وارد کنید.",

    // AI Fix
    "aifix_title" to "تحلیلگر و رفع خطای کد (AI Fix)",
    "aifix_subtitle" to "فرآیند بازبینی انسانی و جایگزینی با قابلیت بازیابی از کرش (Crash-Recoverable Replacement).",
    "aifix_run_check" to "اجرای بررسی کدهای پروژه",
    "aifix_local_lint" to "موارد و خطاهای شناسایی‌شده",
    "aifix_proposal_title" to "پیشنهاد اصلاح کد (نیازمند تأیید شما)",
    "aifix_apply_btn" to "بازبینی و اعمال تغییر",
    "aifix_verify_badge" to "جایگزینی با قابلیت بازیابی در کرش",
    "aifix_gemini_status" to "وضعیت ارائه‌دهنده هوش مصنوعی",
    "aifix_gemini_info" to "بدون موفقیت ساختگی: کلید GEMINI_API_KEY باید در پنل Secrets اضافه شود.",
    "aifix_no_issues" to "تمام بررسی‌های محلی موفق بودند. ساختار کد سالم است.",

    // Deploy
    "deploy_title" to "استقرار و پیش‌نمایش",
    "deploy_subtitle" to "خط تولید و بسته‌بندی واقعی در محیط موبایل.",
    "deploy_target_preview" to "پیش‌نمایش وب زنده درون‌برنامه‌ای",
    "deploy_target_bundle" to "خروجی بسته وب ایستا (ZIP)",
    "deploy_target_spec" to "تنظیمات مشخصات APK اندروید",
    "deploy_status_note" to "مراحل خروجی به‌صورت کاملاً محلی روی حافظه دستگاه بررسی می‌شوند.",

    // Community
    "community_title" to "جامعه کاربری و اقتصاد توانا",
    "community_subtitle" to "شبکه همکاری ماژولار. هسته توسعه نرم‌افزار کاملاً مستقل و آفلاین باقی می‌ماند.",
    "community_notice" to "معماری ماژولار: هسته IDE به صورت ۱۰۰٪ آفلاین و بدون وابستگی به شبکه دوردست کار می‌کند.",
    "community_profile" to "پروفایل توسعه‌دهنده",
    "community_rooms" to "اتاق‌های موضوعی گفتگو",
    "community_collab" to "فرصت‌های همکاری و نقش‌ها",
    "community_xp_ledger" to "دفتر کل امتیاز و پاداش (XP Ledger)",
    "community_leagues" to "لیگ‌ها و نظام مشارکت",
    "community_disclaimer" to "امتیاز XP یا خریدهای واجد شرایط هرگز متضمن دریافت زمین، استخدام، سمت مدیریتی یا کرسی شورا نیستند.",

    // Tutorial
    "tutorial_title" to "آموزش کار با برنامه",
    "tutorial_subtitle" to "راهنمای تصویری و گام به گام مهندسی نرم‌افزار روی موبایل.",
    "tutorial_step1_title" to "۱. ساخت یا انتخاب پروژه",
    "tutorial_step1_desc" to "یک قالب آماده برگزینید یا از صفر شروع کنید. تمام داده‌ها در حافظه پایدار گوشی ذخیره می‌شوند.",
    "tutorial_step2_title" to "۲. کدنویسی سریع و بدون دردسر",
    "tutorial_step2_desc" to "با نوار ابزار نمادها بدون تغییر صفحه‌کلید، کروشه، پرانتز و عملگرها را درج کنید.",
    "tutorial_step3_title" to "۳. آزمایش و اجرای محلی",
    "tutorial_step3_desc" to "در کنسول فرمان کدهای خود را بیازمایید و خطاهای نحوی را بررسی نمایید.",
    "tutorial_step4_title" to "۴. مدیریت تغییرات در گیت",
    "tutorial_step4_desc" to "تغییرات را کامیت کنید تا نسخه‌های مختلف برنامه در دسترستان بماند.",

    // Help
    "help_title" to "علائم و راهنمای تصویری",
    "help_subtitle" to "مفهوم علائم و نشانه‌های ترافیکی نرم‌افزار.",
    "help_traffic_green" to "دایره سبز: وضعیت سالم و بدون تغییر ذخیره‌نشده.",
    "help_traffic_amber" to "لوزی کهربایی: فایل‌های ویرایش‌شده یا نیاز به ذخیره.",
    "help_traffic_red" to "هشت‌ضلعی قرمز: خطای ساختاری یا عدم تنظیمات الزامی.",
    "help_traffic_gray" to "مربع خاکستری: سرویس غیرفعال یا پیکربندی‌نشده.",

    // Settings
    "settings_title" to "تنظیمات کارگاه توانا",
    "settings_theme" to "حالت پوسته و رنگ",
    "theme_dark" to "تیره (اسلیت و کهربا)",
    "theme_light" to "روشن (تیتانیوم)",
    "theme_high_contrast" to "کنتراست بالا (ویژه دسترس‌پذیری)",
    "settings_language" to "زبان برنامه",
    "settings_font_scale" to "اندازه فونت متون",
    "settings_reduced_motion" to "کاهش پویانمایی (Reduced Motion)",
    "settings_reduced_motion_desc" to "پویانمایی‌ها را برای تمرکز بیشتر یا راحتی چشم غیرفعال می‌کند.",
    "settings_reset_demo" to "بارگذاری پروژه‌های نمونه",
    "settings_reset_done" to "پروژه نمونه با موفقیت در حافظه قرار گرفت."
  )

  private val arStrings = mapOf(
    "app_title" to "ABLE Forge",
    "app_subtitle" to "ورشة توانا — هندسة البرمجيات عبر الهاتف",
    "slogan" to "ابنِ. أصلح. ابتكر. في أي مكان.",

    // Nav
    "nav_projects" to "المشاريع",
    "nav_files" to "الملفات",
    "nav_editor" to "المحرر",
    "nav_git" to "Git",
    "nav_console" to "الطرفية",
    "nav_aifix" to "إصلاح ذكي",
    "nav_deploy" to "النشر",
    "nav_community" to "المجتمع",
    "nav_tutorial" to "دليل الاستخدام",
    "nav_help" to "دليل بصري",
    "nav_settings" to "الإعدادات",

    // Dual statuses
    "status_ready" to "جاهز",
    "status_modified" to "معدل",
    "status_issue" to "تنبيه",
    "status_unconfigured" to "غير مهيأ",
    "status_clean" to "نظيف",

    // Traffic Flow Steps
    "step_1_create" to "١. إنشاء المشروع",
    "step_2_code" to "٢. كتابة الكود",
    "step_3_verify" to "٣. الفحص والاختبار",
    "step_4_commit" to "٤. حفظ في Git",
    "step_5_deploy" to "٥. النشر والمعاينة",

    // Actions
    "action_new_project" to "مشروع جديد",
    "action_new_file" to "ملف جديد",
    "action_save" to "حفظ الملف",
    "action_run" to "تشغيل",
    "action_commit" to "تثبيت (Commit)",
    "action_cancel" to "إلغاء",
    "action_delete" to "حذف",
    "action_clear" to "مسح",
    "action_refresh" to "تحديث",
    "action_undo" to "تراجع",
    "action_redo" to "إعادة",

    // Common
    "projects_title" to "مساحات العمل والمشاريع",
    "projects_desc" to "مشاريع حقيقية تعمل بدون إنترنت ومخزنة محلياً في الهاتف.",
    "projects_empty" to "لا توجد مشاريع حتى الآن. اضغط '+' للبدء.",
    "editor_title" to "محرر الأكواد المتنقل",
    "editor_symbol_bar" to "شريط الرموز السريعة لشاشة اللمس",
    "git_title" to "إدارة النسخ Git المحلية",
    "console_title" to "وحدة أوامر الهاتف المحمول",
    "aifix_title" to "مدقق الأكواد والإصلاح الذكي",
    "deploy_title" to "النشر والتوزيع",
    "tutorial_title" to "دليل استخدام ورشة توانا",
    "help_title" to "الإرشادات والعلامات البصرية",
    "settings_title" to "إعدادات التطبيق",
    "theme_dark" to "داكن",
    "theme_light" to "فاتح",
    "theme_high_contrast" to "تباين فائق (إمكانية الوصول)",
    "settings_reduced_motion" to "تقليل الحركة",
    "settings_reduced_motion_desc" to "إيقاف الحركات الانتقالية لراحة العين."
  )

  private val esStrings = mapOf(
    "app_title" to "ABLE Forge",
    "app_subtitle" to "Taller Hábil — Ingeniería de software móvil",
    "slogan" to "Construye. Repara. Crea. Donde sea.",

    // Nav
    "nav_projects" to "Proyectos",
    "nav_files" to "Archivos",
    "nav_editor" to "Editor",
    "nav_git" to "Git",
    "nav_console" to "Consola",
    "nav_aifix" to "AI Fix",
    "nav_deploy" to "Despliegue",
    "nav_community" to "Comunidad",
    "nav_tutorial" to "Tutorial",
    "nav_help" to "Guía Visual",
    "nav_settings" to "Ajustes",

    // Dual statuses
    "status_ready" to "LISTO",
    "status_modified" to "MODIFICADO",
    "status_issue" to "ERROR / REVISAR",
    "status_unconfigured" to "NO CONFIGURADO",
    "status_clean" to "LIMPIO",

    // Traffic Flow Steps
    "step_1_create" to "1. Crear Proyecto",
    "step_2_code" to "2. Escribir Código",
    "step_3_verify" to "3. Probar y Corregir",
    "step_4_commit" to "4. Confirmar Git",
    "step_5_deploy" to "5. Desplegar",

    // Actions
    "action_new_project" to "Nuevo Proyecto",
    "action_new_file" to "Nuevo Archivo",
    "action_save" to "Guardar",
    "action_run" to "Ejecutar",
    "action_commit" to "Confirmar",
    "action_cancel" to "Cancelar",
    "action_delete" to "Eliminar",
    "action_clear" to "Limpiar",
    "action_refresh" to "Actualizar",
    "action_undo" to "Deshacer",
    "action_redo" to "Rehacer",

    // Common
    "projects_title" to "Espacios de Trabajo y Proyectos",
    "projects_desc" to "Proyectos reales guardados localmente en el dispositivo.",
    "projects_empty" to "No hay proyectos aún. Pulsa '+' para crear uno.",
    "editor_title" to "Editor de Código Móvil",
    "editor_symbol_bar" to "Barra de Símbolos Rápidos para Móvil",
    "git_title" to "Control Git Local",
    "console_title" to "Consola de Comandos Móvil",
    "aifix_title" to "Corrector de Código y Sintaxis",
    "deploy_title" to "Despliegue y Vista Previa",
    "tutorial_title" to "Tutorial de Uso",
    "help_title" to "Guía de Señales Visuales",
    "settings_title" to "Ajustes de Forge",
    "theme_dark" to "Oscuro Pizarra",
    "theme_light" to "Claro Titanio",
    "theme_high_contrast" to "Alto Contraste (Accesibilidad)",
    "settings_reduced_motion" to "Movimiento Reducido",
    "settings_reduced_motion_desc" to "Desactiva animaciones no esenciales."
  )

  private val zhStrings = mapOf(
    "app_title" to "ABLE Forge",
    "app_subtitle" to "移动端软件开发工坊",
    "slogan" to "构建。修复。创造。随处随行。",
    "nav_projects" to "项目",
    "nav_files" to "文件",
    "nav_editor" to "编辑器",
    "nav_git" to "Git版本",
    "nav_console" to "控制台",
    "nav_aifix" to "智能修复",
    "nav_deploy" to "部署",
    "nav_community" to "社区",
    "nav_tutorial" to "教程",
    "nav_help" to "视觉指南",
    "nav_settings" to "设置"
  )

  private val ruStrings = mapOf(
    "app_title" to "ABLE Forge",
    "app_subtitle" to "Мобильная кузница разработки ПО",
    "slogan" to "Создавай. Исправляй. Твори. Везде.",
    "nav_projects" to "Проекты",
    "nav_files" to "Файлы",
    "nav_editor" to "Редактор",
    "nav_git" to "Git",
    "nav_console" to "Консоль",
    "nav_aifix" to "AI Исправление",
    "nav_deploy" to "Развертывание",
    "nav_community" to "Сообщество",
    "nav_tutorial" to "Обучение",
    "nav_help" to "Справка",
    "nav_settings" to "Настройки"
  )
}
