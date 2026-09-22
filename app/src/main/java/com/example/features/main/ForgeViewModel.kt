package com.example.features.main

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.data.ForgeDatabase
import com.example.core.data.ForgeRepository
import com.example.core.guidance.AdaptiveUserMetrics
import com.example.core.guidance.GuidanceSelectionMode
import com.example.core.guidance.GuidanceState
import com.example.core.guidance.GuidanceTier
import com.example.core.localization.ForgeLanguage
import com.example.core.model.ConsoleEntryEntity
import com.example.core.model.FileEntity
import com.example.core.model.GitCommitEntity
import com.example.core.model.ProjectEntity
import com.example.ui.theme.ForgeThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class ForgeSection {
  PROJECTS,
  FILES,
  EDITOR,
  GIT,
  CONSOLE,
  AI_FIX,
  DEPLOY,
  COMMUNITY,
  TUTORIAL,
  HELP,
  SETTINGS
}

data class DiagnosticIssue(
  val fileName: String,
  val line: Int,
  val message: String,
  val severity: DiagnosticSeverity
)

enum class DiagnosticSeverity {
  ERROR,
  WARNING,
  INFO
}

data class ForgeUiState(
  val currentSection: ForgeSection = ForgeSection.PROJECTS,
  val activeProject: ProjectEntity? = null,
  val activeFile: FileEntity? = null,
  val editorText: String = "",
  val isEditorDirty: Boolean = false,
  val projects: List<ProjectEntity> = emptyList(),
  val files: List<FileEntity> = emptyList(),
  val commits: List<GitCommitEntity> = emptyList(),
  val consoleHistory: List<ConsoleEntryEntity> = emptyList(),
  val themeMode: ForgeThemeMode = ForgeThemeMode.DARK,
  val language: ForgeLanguage = ForgeLanguage.FA, // Defaults to Persian / فارسی
  val reducedMotion: Boolean = false,
  val fontScale: Float = 1.0f,
  val statusMessage: String? = null,
  val diagnosticIssues: List<DiagnosticIssue> = emptyList(),
  val geminiKeyConfigured: Boolean = false,
  val guidanceState: GuidanceState = GuidanceState(),
  val manualTierSheetOpen: Boolean = false
)

class ForgeViewModel(application: Application) : AndroidViewModel(application) {
  private val repository: ForgeRepository
  private val prefs = application.getSharedPreferences("able_forge_guidance", Context.MODE_PRIVATE)

  private val _currentSection = MutableStateFlow(ForgeSection.PROJECTS)
  private val _activeProject = MutableStateFlow<ProjectEntity?>(null)
  private val _activeFile = MutableStateFlow<FileEntity?>(null)
  private val _editorText = MutableStateFlow("")
  private val _isEditorDirty = MutableStateFlow(false)
  private val _themeMode = MutableStateFlow(ForgeThemeMode.DARK)
  private val _language = MutableStateFlow(ForgeLanguage.FA)
  private val _reducedMotion = MutableStateFlow(false)
  private val _fontScale = MutableStateFlow(1.0f)
  private val _statusMessage = MutableStateFlow<String?>(null)
  private val _diagnosticIssues = MutableStateFlow<List<DiagnosticIssue>>(emptyList())
  private val _geminiKeyConfigured = MutableStateFlow(false)
  private val _guidanceState = MutableStateFlow(GuidanceState())
  private val _manualTierSheetOpen = MutableStateFlow(false)

  private val _activeProjectFiles = MutableStateFlow<List<FileEntity>>(emptyList())
  private val _activeProjectCommits = MutableStateFlow<List<GitCommitEntity>>(emptyList())

  val uiState: StateFlow<ForgeUiState>

  init {
    val db = ForgeDatabase.getDatabase(application)
    repository = ForgeRepository(db)

    // Load initial guidance settings from shared preferences
    val hasChosenBefore = prefs.getBoolean("guidance_configured", false)
    val savedMode = prefs.getString("guidance_mode", GuidanceSelectionMode.UNSET.name) ?: GuidanceSelectionMode.UNSET.name
    val savedTier = prefs.getString("guidance_tier", GuidanceTier.TEACHING_GUIDANCE_ASSISTANT.name) ?: GuidanceTier.TEACHING_GUIDANCE_ASSISTANT.name
    val savedDnd = prefs.getBoolean("guidance_dnd", false)

    _guidanceState.value = GuidanceState(
      isFirstLaunchPromptActive = !hasChosenBefore,
      selectionMode = GuidanceSelectionMode.valueOf(savedMode),
      currentTier = GuidanceTier.valueOf(savedTier),
      doNotDisturb = savedDnd
    )

    // Check if API key is present in environment/BuildConfig
    try {
      val geminiKey = com.example.BuildConfig::class.java.getField("GEMINI_API_KEY").get(null) as? String
      _geminiKeyConfigured.value = !geminiKey.isNullOrBlank() && geminiKey != "MY_GEMINI_API_KEY"
    } catch (_: Exception) {
      _geminiKeyConfigured.value = false
    }

    viewModelScope.launch {
      repository.ensureInitialData()
    }

    // Combine flows for the UI state
    uiState = combine(
      combine(
        _currentSection,
        _activeProject,
        _activeFile,
        _editorText,
        _isEditorDirty
      ) { section, project, file, editor, dirty ->
        Tuple5(section, project, file, editor, dirty)
      },
      combine(
        repository.allProjects,
        _activeProjectFiles,
        _activeProjectCommits,
        repository.consoleHistory
      ) { projects, files, commits, console ->
        Tuple4(projects, files, commits, console)
      },
      combine(
        _themeMode,
        _language,
        _reducedMotion,
        _fontScale,
        _statusMessage
      ) { theme, lang, motion, scale, msg ->
        Tuple5(theme, lang, motion, scale, msg)
      },
      combine(
        _diagnosticIssues,
        _geminiKeyConfigured,
        _guidanceState,
        _manualTierSheetOpen
      ) { issues, keyConfigured, guidance, sheetOpen ->
        Tuple4(issues, keyConfigured, guidance, sheetOpen)
      }
    ) { t1, t2, t3, t4 ->
      // Auto-select first project if none selected
      if (_activeProject.value == null && t2.a.isNotEmpty()) {
        selectProject(t2.a.first())
      }

      ForgeUiState(
        currentSection = t1.a,
        activeProject = t1.b,
        activeFile = t1.c,
        editorText = t1.d,
        isEditorDirty = t1.e,
        projects = t2.a,
        files = t2.b,
        commits = t2.c,
        consoleHistory = t2.d,
        themeMode = t3.a,
        language = t3.b,
        reducedMotion = t3.c,
        fontScale = t3.d,
        statusMessage = t3.e,
        diagnosticIssues = t4.a,
        geminiKeyConfigured = t4.b,
        guidanceState = t4.c,
        manualTierSheetOpen = t4.d
      )
    }.stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = ForgeUiState()
    )
  }

  // --- Guidance System Actions ---

  fun chooseManualGuidance() {
    _guidanceState.value = _guidanceState.value.copy(
      isFirstLaunchPromptActive = false,
      selectionMode = GuidanceSelectionMode.MANUAL
    )
    _manualTierSheetOpen.value = true
    saveGuidancePreferences()
  }

  fun chooseAdaptiveGuidance() {
    _guidanceState.value = _guidanceState.value.copy(
      isFirstLaunchPromptActive = false,
      selectionMode = GuidanceSelectionMode.ADAPTIVE,
      currentTier = GuidanceTier.TEACHING_GUIDANCE_ASSISTANT // Starts at helpful level, adapts smoothly
    )
    _manualTierSheetOpen.value = false
    saveGuidancePreferences()
    _statusMessage.value = "سیستم راهنمایی هوشمند فعال شد."
  }

  fun setGuidanceTier(tier: GuidanceTier) {
    _guidanceState.value = _guidanceState.value.copy(
      currentTier = tier,
      masterySuggestionVisible = false
    )
    _manualTierSheetOpen.value = false
    saveGuidancePreferences()
  }

  fun openManualTierSelection() {
    _manualTierSheetOpen.value = true
  }

  fun closeManualTierSelection() {
    _manualTierSheetOpen.value = false
  }

  fun toggleDoNotDisturb() {
    val newDnd = !_guidanceState.value.doNotDisturb
    _guidanceState.value = _guidanceState.value.copy(doNotDisturb = newDnd)
    prefs.edit().putBoolean("guidance_dnd", newDnd).apply()
    _statusMessage.value = if (newDnd) "حالت فعلاً مزاحم نشو فعال شد." else "راهنمایی از سر گرفته شد."
  }

  fun dismissMasterySuggestion() {
    _guidanceState.value = _guidanceState.value.copy(masterySuggestionVisible = false)
  }

  fun acceptMasterySuggestion() {
    // User voluntarily chooses to move to SELF or lower tier
    val current = _guidanceState.value.currentTier
    val newTier = when (current) {
      GuidanceTier.TEACHING_GUIDANCE_ASSISTANT -> GuidanceTier.TEACHING_GUIDANCE
      GuidanceTier.TEACHING_GUIDANCE -> GuidanceTier.GUIDANCE
      GuidanceTier.GUIDANCE -> GuidanceTier.SELF
      GuidanceTier.SELF -> GuidanceTier.SELF
    }
    _guidanceState.value = _guidanceState.value.copy(
      currentTier = newTier,
      masterySuggestionVisible = false
    )
    saveGuidancePreferences()
    _statusMessage.value = "سطح راهنمایی به انتخاب شما تنظیم شد."
  }

  fun recordHelpRequested() {
    val m = _guidanceState.value.metrics
    val updatedMetrics = m.copy(helpRequestsCount = m.helpRequestsCount + 1)
    _guidanceState.value = _guidanceState.value.copy(metrics = updatedMetrics)
    if (_guidanceState.value.selectionMode == GuidanceSelectionMode.ADAPTIVE) {
      // If user frequently asks for help, upgrade tier if below maximum
      if (_guidanceState.value.currentTier == GuidanceTier.SELF) {
        _guidanceState.value = _guidanceState.value.copy(currentTier = GuidanceTier.GUIDANCE)
      } else if (_guidanceState.value.currentTier == GuidanceTier.GUIDANCE) {
        _guidanceState.value = _guidanceState.value.copy(currentTier = GuidanceTier.TEACHING_GUIDANCE)
      }
      saveGuidancePreferences()
    }
  }

  fun recordActionSuccess(actionKey: String) {
    val m = _guidanceState.value.metrics
    val currentRepeats = (m.actionRepeats[actionKey] ?: 0) + 1
    val newActionRepeats = m.actionRepeats.toMutableMap().apply { put(actionKey, currentRepeats) }
    val newSuccessCount = m.successCount + 1

    val updatedMetrics = m.copy(
      successCount = newSuccessCount,
      actionRepeats = newActionRepeats
    )

    // Check for mastery suggestion if in ADAPTIVE mode:
    // If the same action succeeded 3+ times without error and current tier is above SELF,
    // gently suggest: «به نظر می‌رسد این کار را خوب یاد گرفته‌ای. دوست داری این بار خودت انجامش بدهی؟»
    val shouldSuggestMastery = (_guidanceState.value.selectionMode == GuidanceSelectionMode.ADAPTIVE) &&
        (currentRepeats >= 3) &&
        (_guidanceState.value.currentTier != GuidanceTier.SELF) &&
        (!_guidanceState.value.doNotDisturb)

    _guidanceState.value = _guidanceState.value.copy(
      metrics = updatedMetrics,
      masterySuggestionVisible = shouldSuggestMastery
    )
  }

  private fun saveGuidancePreferences() {
    prefs.edit()
      .putBoolean("guidance_configured", true)
      .putString("guidance_mode", _guidanceState.value.selectionMode.name)
      .putString("guidance_tier", _guidanceState.value.currentTier.name)
      .putBoolean("guidance_dnd", _guidanceState.value.doNotDisturb)
      .apply()
  }

  // --- Core Navigation and Workflows ---

  fun setSection(section: ForgeSection) {
    _currentSection.value = section
    // Record action transition
    val repeats = (_guidanceState.value.metrics.actionRepeats[section.name] ?: 0) + 1
    val updatedMap = _guidanceState.value.metrics.actionRepeats.toMutableMap().apply { put(section.name, repeats) }
    _guidanceState.value = _guidanceState.value.copy(
      metrics = _guidanceState.value.metrics.copy(actionRepeats = updatedMap)
    )
  }

  fun selectProject(project: ProjectEntity) {
    _activeProject.value = project
    viewModelScope.launch {
      repository.getFilesForProject(project.id).collect { files ->
        _activeProjectFiles.value = files
        // Auto open the first editable file if no file selected or file belongs to another project
        if (_activeFile.value == null || _activeFile.value?.projectId != project.id) {
          val firstCodeFile = files.find { !it.isDirectory }
          if (firstCodeFile != null) {
            selectFile(firstCodeFile)
          }
        }
      }
    }
    viewModelScope.launch {
      repository.getCommitsForProject(project.id).collect { commits ->
        _activeProjectCommits.value = commits
      }
    }
  }

  fun selectFile(file: FileEntity) {
    _activeFile.value = file
    _editorText.value = file.content
    _isEditorDirty.value = false
  }

  fun onEditorTextChanged(newText: String) {
    _editorText.value = newText
    _isEditorDirty.value = (_activeFile.value?.content != newText)
  }

  fun insertEditorSymbol(symbol: String) {
    _editorText.value = _editorText.value + symbol
    _isEditorDirty.value = true
  }

  fun saveCurrentFile() {
    val file = _activeFile.value ?: return
    viewModelScope.launch {
      val updated = file.copy(content = _editorText.value)
      repository.updateFile(updated)
      _activeFile.value = updated
      _isEditorDirty.value = false
      _statusMessage.value = "فایل در حافظه ذخیره شد."
      recordActionSuccess("save_file")
    }
  }

  fun createProject(name: String, description: String, templateType: String) {
    viewModelScope.launch {
      val newId = repository.createProject(name, description, templateType)
      val created = repository.getProjectById(newId)
      if (created != null) {
        selectProject(created)
        setSection(ForgeSection.EDITOR)
        recordActionSuccess("create_project")
      }
    }
  }

  fun deleteProject(project: ProjectEntity) {
    viewModelScope.launch {
      repository.deleteProject(project)
      if (_activeProject.value?.id == project.id) {
        _activeProject.value = null
        _activeFile.value = null
      }
    }
  }

  fun createFile(name: String, content: String = "") {
    val project = _activeProject.value ?: return
    viewModelScope.launch {
      val newFile = FileEntity(
        projectId = project.id,
        path = name.trim(),
        name = name.trim(),
        content = content
      )
      repository.insertFile(newFile)
      selectFile(newFile)
      setSection(ForgeSection.EDITOR)
      recordActionSuccess("create_file")
    }
  }

  fun deleteFile(file: FileEntity) {
    viewModelScope.launch {
      repository.deleteFile(file)
      if (_activeFile.value?.id == file.id) {
        _activeFile.value = null
        _editorText.value = ""
      }
    }
  }

  fun commitGitChanges(message: String) {
    val project = _activeProject.value ?: return
    viewModelScope.launch {
      val hash = repository.commitChanges(project.id, message)
      _statusMessage.value = "تغییرات با موفقیت ثبت شد [$hash]"
      recordActionSuccess("git_commit")
    }
  }

  fun runConsoleCommand(command: String) {
    val project = _activeProject.value
    val projectId = project?.id ?: 0L
    val files = _activeProjectFiles.value
    viewModelScope.launch {
      repository.executeConsoleCommand(projectId, command, files)
      recordActionSuccess("console_command")
    }
  }

  fun runDiagnostics() {
    val files = _activeProjectFiles.value
    val issues = mutableListOf<DiagnosticIssue>()

    files.forEach { file ->
      if (file.name.endsWith(".json")) {
        val trimmed = file.content.trim()
        if (!trimmed.startsWith("{") || !trimmed.endsWith("}")) {
          issues.add(
            DiagnosticIssue(
              fileName = file.name,
              line = 1,
              message = "خطای ساختار JSON: باید با { شروع و با } بسته شود.",
              severity = DiagnosticSeverity.ERROR
            )
          )
        }
      }
      if (file.name.endsWith(".html")) {
        val openTags = Regex("<(\\w+)>").findAll(file.content).map { it.groupValues[1] }.toList()
        val closeTags = Regex("</(\\w+)>").findAll(file.content).map { it.groupValues[1] }.toList()
        openTags.filter { it in listOf("div", "p", "span", "card", "body", "html") }.forEach { tag ->
          if (openTags.count { it == tag } != closeTags.count { it == tag }) {
            issues.add(
              DiagnosticIssue(
                fileName = file.name,
                line = 1,
                message = "تگ باز مانده: تعداد <$tag> با </$tag> برابر نیست.",
                severity = DiagnosticSeverity.WARNING
              )
            )
          }
        }
      }
      if (file.name.endsWith(".kt")) {
        val openBraces = file.content.count { it == '{' }
        val closeBraces = file.content.count { it == '}' }
        if (openBraces != closeBraces) {
          issues.add(
            DiagnosticIssue(
              fileName = file.name,
              line = 1,
              message = "عدم تعادل آکولاد: $openBraces آکولاد باز '{' در برابر $closeBraces بسته '}'",
              severity = DiagnosticSeverity.ERROR
            )
          )
        }
      }
    }

    if (issues.isNotEmpty()) {
      val m = _guidanceState.value.metrics
      _guidanceState.value = _guidanceState.value.copy(
        metrics = m.copy(recentErrorsCount = m.recentErrorsCount + issues.size)
      )
    } else {
      recordActionSuccess("diagnostics_clean")
    }

    _diagnosticIssues.value = issues
  }

  fun setThemeMode(mode: ForgeThemeMode) {
    _themeMode.value = mode
  }

  fun setLanguage(lang: ForgeLanguage) {
    _language.value = lang
  }

  fun setReducedMotion(reduced: Boolean) {
    _reducedMotion.value = reduced
  }

  fun setFontScale(scale: Float) {
    _fontScale.value = scale
  }

  fun clearStatusMessage() {
    _statusMessage.value = null
  }
}

private data class Tuple4<A, B, C, D>(val a: A, val b: B, val c: C, val d: D)
private data class Tuple5<A, B, C, D, E>(val a: A, val b: B, val c: C, val d: D, val e: E)
