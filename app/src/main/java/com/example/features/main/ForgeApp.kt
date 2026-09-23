package com.example.features.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CallSplit
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Workspaces
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.core.guidance.ForgeVoiceReader
import com.example.core.guidance.rememberForgeVoiceReader
import com.example.core.localization.ForgeLanguage
import com.example.core.localization.ForgeStrings
import com.example.core.voice.SttTargetField
import com.example.features.aifix.AiFixScreen
import com.example.features.community.CommunityScreen
import com.example.features.console.ConsoleScreen
import com.example.features.deploy.DeployScreen
import com.example.features.editor.EditorScreen
import com.example.features.files.FilesScreen
import com.example.features.git.GitScreen
import com.example.features.guidance.ContextualGuidanceBanner
import com.example.features.guidance.FirstLaunchGuidanceDialog
import com.example.features.guidance.ManualTierSelectionDialog
import com.example.features.help.HelpScreen
import com.example.features.monetization.MonetizationScreen
import com.example.features.projects.ProjectsScreen
import com.example.features.settings.SettingsScreen
import com.example.features.tutorial.TutorialScreen
import com.example.ui.components.OfflineNoticeBanner
import com.example.ui.components.OnlineSyncStatus
import com.example.ui.components.SttVoiceInputDialog
import com.example.ui.components.TrafficSignalType
import com.example.ui.components.TrafficStepFlowIndicator
import com.example.ui.components.UniversalTrafficPill
import com.example.ui.components.VoiceCaptionBar
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgeApp(
  viewModel: ForgeViewModel,
  modifier: Modifier = Modifier
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
  val scope = rememberCoroutineScope()
  val snackbarHostState = remember { SnackbarHostState() }
  val voiceReader = rememberForgeVoiceReader()

  LaunchedEffect(uiState.statusMessage) {
    uiState.statusMessage?.let { msg ->
      snackbarHostState.showSnackbar(msg)
      viewModel.clearStatusMessage()
    }
  }

  MyApplicationTheme(
    themeMode = uiState.themeMode,
    reducedMotion = uiState.reducedMotion,
    fontScale = uiState.fontScale
  ) {
    // Provide correct layout direction according to active language (RTL for FA & AR, LTR for EN, ES, ZH, RU)
    CompositionLocalProvider(
      LocalLayoutDirection provides uiState.language.layoutDirection
    ) {
      // 1. Mandatory First Launch Dialog
      FirstLaunchGuidanceDialog(
        isOpen = uiState.guidanceState.isFirstLaunchPromptActive,
        language = uiState.language,
        onChooseManual = { viewModel.chooseManualGuidance() },
        onChooseAdaptive = { viewModel.chooseAdaptiveGuidance() },
        voiceReader = voiceReader
      )

      // 2. Manual Tier Selection Dialog (A, B, C, D)
      ManualTierSelectionDialog(
        isOpen = uiState.manualTierSheetOpen,
        currentTier = uiState.guidanceState.currentTier,
        language = uiState.language,
        onSelectTier = { tier -> viewModel.setGuidanceTier(tier) },
        onDismiss = { viewModel.closeManualTierSelection() }
      )

      // 3. Speech-to-Text Multi-Field Dialog
      SttVoiceInputDialog(
        isOpen = uiState.sttDialogOpen,
        targetTitle = uiState.sttTarget.faLabel,
        isListening = uiState.sttListening,
        transcribedText = uiState.sttTranscribed,
        lastError = uiState.sttError,
        onStartListening = { viewModel.startSttListening() },
        onStopListening = { viewModel.stopSttListening() },
        onConfirmText = { text -> viewModel.applySttResult(text) },
        onDismiss = { viewModel.closeSttDialog() }
      )

      ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
          ModalDrawerSheet(
            modifier = Modifier.width(300.dp)
          ) {
            Column(
              modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
            ) {
              // Drawer Brand Header
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
              ) {
                Box(
                  contentAlignment = Alignment.Center,
                  modifier = Modifier
                    .size(44.dp)
                    .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(10.dp))
                ) {
                  Text(
                    text = "AF",
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                  )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                  Text(
                    text = ForgeStrings.get("app_title", uiState.language),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                  )
                  Text(
                    text = ForgeStrings.get("app_subtitle", uiState.language),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
              }

              Text(
                text = ForgeStrings.get("slogan", uiState.language),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
              )

              HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
              Spacer(modifier = Modifier.height(10.dp))

              // Drawer Navigation Items
              val drawerItems = listOf(
                Pair(ForgeSection.PROJECTS, Icons.Default.Workspaces),
                Pair(ForgeSection.FILES, Icons.Default.Folder),
                Pair(ForgeSection.EDITOR, Icons.Default.Code),
                Pair(ForgeSection.GIT, Icons.Default.CallSplit),
                Pair(ForgeSection.CONSOLE, Icons.Default.Terminal),
                Pair(ForgeSection.AI_FIX, Icons.Default.AutoAwesome),
                Pair(ForgeSection.DEPLOY, Icons.Default.RocketLaunch),
                Pair(ForgeSection.COMMUNITY, Icons.Default.Group),
                Pair(ForgeSection.MONETIZATION, Icons.Default.Paid),
                Pair(ForgeSection.TUTORIAL, Icons.Default.MenuBook),
                Pair(ForgeSection.HELP, Icons.Default.HelpOutline),
                Pair(ForgeSection.SETTINGS, Icons.Default.Settings)
              )

              drawerItems.forEach { (section, icon) ->
                val labelKey = when (section) {
                  ForgeSection.PROJECTS -> "nav_projects"
                  ForgeSection.FILES -> "nav_files"
                  ForgeSection.EDITOR -> "nav_editor"
                  ForgeSection.GIT -> "nav_git"
                  ForgeSection.CONSOLE -> "nav_console"
                  ForgeSection.AI_FIX -> "nav_aifix"
                  ForgeSection.DEPLOY -> "nav_deploy"
                  ForgeSection.COMMUNITY -> "nav_community"
                  ForgeSection.MONETIZATION -> "nav_monetization"
                  ForgeSection.TUTORIAL -> "nav_tutorial"
                  ForgeSection.HELP -> "nav_help"
                  ForgeSection.SETTINGS -> "nav_settings"
                }

                NavigationDrawerItem(
                  icon = { Icon(icon, contentDescription = null) },
                  label = { Text(ForgeStrings.get(labelKey, uiState.language), fontWeight = FontWeight.Medium) },
                  selected = (uiState.currentSection == section),
                  onClick = {
                    viewModel.setSection(section)
                    scope.launch { drawerState.close() }
                  },
                  modifier = Modifier
                    .padding(NavigationDrawerItemDefaults.ItemPadding)
                    .testTag("drawer_item_${section.name.lowercase()}")
                )
              }
            }
          }
        }
      ) {
        Scaffold(
          topBar = {
            Column(modifier = Modifier.statusBarsPadding()) {
              TopAppBar(
                title = {
                  Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Text(
                        text = "ABLE Forge",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                      )
                      Spacer(modifier = Modifier.width(6.dp))
                      Text(
                        text = "کارگاه توانا",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                      )
                    }
                    Text(
                      text = uiState.activeProject?.let { "~/${it.name}/${uiState.activeFile?.name ?: ""}" } ?: "~/no-project",
                      style = MaterialTheme.typography.bodySmall,
                      color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                  }
                },
                navigationIcon = {
                  IconButton(
                    onClick = { scope.launch { drawerState.open() } },
                    modifier = Modifier.testTag("btn_open_drawer")
                  ) {
                    Icon(Icons.Default.Menu, contentDescription = "Menu")
                  }
                },
                actions = {
                  // STT Voice Input Trigger (Question, Command, Error, Text)
                  IconButton(
                    onClick = {
                      val target = when (uiState.currentSection) {
                        ForgeSection.CONSOLE -> SttTargetField.COMMAND
                        ForgeSection.EDITOR -> SttTargetField.TEXT
                        ForgeSection.AI_FIX -> SttTargetField.ERROR
                        else -> SttTargetField.QUESTION
                      }
                      viewModel.openSttDialog(target)
                    },
                    modifier = Modifier.testTag("btn_mic_action")
                  ) {
                    Icon(
                      Icons.Default.Mic,
                      contentDescription = "ورودی صوتی (گفتار به متن)",
                      tint = MaterialTheme.colorScheme.primary
                    )
                  }

                  // Permanent Universal Help Button
                  Button(
                    onClick = {
                      viewModel.recordHelpRequested()
                      viewModel.openManualTierSelection()
                    },
                    colors = ButtonDefaults.buttonColors(
                      containerColor = MaterialTheme.colorScheme.primaryContainer,
                      contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    shape = RoundedCornerShape(18.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier
                      .padding(end = 6.dp)
                      .testTag("btn_permanent_help")
                  ) {
                    Icon(Icons.Default.Help, contentDescription = "کمک", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                      text = if (uiState.language == ForgeLanguage.FA) "کمک" else "Help",
                      fontWeight = FontWeight.Bold,
                      fontSize = 12.sp
                    )
                  }

                  // Online Sync Status (PENDING / SYNCED / OFFLINE)
                  UniversalTrafficPill(
                    signal = uiState.remoteSyncStatus.signal,
                    label = if (uiState.language == ForgeLanguage.FA) uiState.remoteSyncStatus.faLabel else uiState.remoteSyncStatus.enLabel,
                    modifier = Modifier.padding(end = 4.dp)
                  )

                  UniversalTrafficPill(
                    signal = if (uiState.isEditorDirty) TrafficSignalType.MODIFIED else TrafficSignalType.READY,
                    label = if (uiState.isEditorDirty) "UNSAVED" else "CLEAN",
                    modifier = Modifier.padding(end = 8.dp)
                  )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                  containerColor = MaterialTheme.colorScheme.surface
                )
              )

              // Visual Step Flow Bar: Project -> Files -> Editor -> Console -> Git
              TrafficStepFlowIndicator(
                currentSection = uiState.currentSection,
                onStepClick = { viewModel.setSection(it) }
              )
            }
          },
          bottomBar = {
            NavigationBar(
              containerColor = MaterialTheme.colorScheme.surface,
              tonalElevation = 3.dp,
              modifier = Modifier.navigationBarsPadding()
            ) {
              val bottomItems = listOf(
                Triple(ForgeSection.PROJECTS, Icons.Default.Workspaces, "nav_projects"),
                Triple(ForgeSection.FILES, Icons.Default.Folder, "nav_files"),
                Triple(ForgeSection.EDITOR, Icons.Default.Code, "nav_editor"),
                Triple(ForgeSection.CONSOLE, Icons.Default.Terminal, "nav_console"),
                Triple(ForgeSection.GIT, Icons.Default.CallSplit, "nav_git")
              )

              bottomItems.forEach { (section, icon, key) ->
                val isSelected = (uiState.currentSection == section)
                NavigationBarItem(
                  selected = isSelected,
                  onClick = { viewModel.setSection(section) },
                  icon = { Icon(icon, contentDescription = ForgeStrings.get(key, uiState.language)) },
                  label = { Text(ForgeStrings.get(key, uiState.language), fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                  modifier = Modifier.testTag("nav_bottom_${section.name.lowercase()}")
                )
              }
            }
          },
          snackbarHost = { SnackbarHost(snackbarHostState) },
          modifier = modifier.fillMaxSize()
        ) { paddingValues ->
          Column(
            modifier = Modifier
              .fillMaxSize()
              .padding(paddingValues)
          ) {
            // 1. Transparent Offline Notice Banner
            OfflineNoticeBanner(
              isOnline = uiState.isOnline,
              language = uiState.language
            )

            // 2. Voice Caption / Subtitles Bar for Low Vision, Hard of Hearing, and ADHD
            VoiceCaptionBar(
              caption = uiState.ttsCaption,
              isPlaying = uiState.ttsPlaying,
              playbackSpeed = uiState.ttsSpeed,
              onPause = { viewModel.pauseTts() },
              onResume = { viewModel.resumeTts() },
              onStop = { viewModel.stopTts() },
              onSpeedChange = { viewModel.setTtsSpeed(it) }
            )

            // 3. Adaptive & Multi-channel Guidance Banner right below top flow
            ContextualGuidanceBanner(
              section = uiState.currentSection,
              guidanceState = uiState.guidanceState,
              language = uiState.language,
              voiceReader = voiceReader,
              onDoNotDisturbToggle = { viewModel.toggleDoNotDisturb() },
              onRequestMoreHelp = { viewModel.recordHelpRequested() },
              onOpenTutorial = { viewModel.setSection(ForgeSection.TUTORIAL) },
              onDismissMastery = { viewModel.dismissMasterySuggestion() },
              onAcceptMastery = { viewModel.acceptMasterySuggestion() }
            )

            Box(
              modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
            ) {
              when (uiState.currentSection) {
                ForgeSection.PROJECTS -> ProjectsScreen(
                  projects = uiState.projects,
                  activeProject = uiState.activeProject,
                  language = uiState.language,
                  onSelectProject = { viewModel.selectProject(it) },
                  onCreateProject = { name, desc, template -> viewModel.createProject(name, desc, template) },
                  onDeleteProject = { viewModel.deleteProject(it) }
                )
                ForgeSection.FILES -> FilesScreen(
                  project = uiState.activeProject,
                  files = uiState.files,
                  activeFile = uiState.activeFile,
                  language = uiState.language,
                  onOpenFile = { file ->
                    viewModel.selectFile(file)
                    viewModel.setSection(ForgeSection.EDITOR)
                  },
                  onCreateFile = { name -> viewModel.createFile(name) },
                  onDeleteFile = { file -> viewModel.deleteFile(file) }
                )
                ForgeSection.EDITOR -> EditorScreen(
                  activeFile = uiState.activeFile,
                  editorText = uiState.editorText,
                  isDirty = uiState.isEditorDirty,
                  language = uiState.language,
                  onTextChanged = { viewModel.onEditorTextChanged(it) },
                  onSave = { viewModel.saveCurrentFile() },
                  onSymbolClick = { sym -> viewModel.insertEditorSymbol(sym) },
                  onJumpToConsole = { viewModel.setSection(ForgeSection.CONSOLE) }
                )
                ForgeSection.GIT -> GitScreen(
                  project = uiState.activeProject,
                  commits = uiState.commits,
                  isDirty = uiState.isEditorDirty,
                  hasAbleFlag = uiState.hasAbleFlag,
                  language = uiState.language,
                  onCommit = { msg: String -> viewModel.commitGitChanges(msg) },
                  onAddAll = { viewModel.gitAddAll() },
                  onPush = { viewModel.gitPush() },
                  onPull = { viewModel.gitPull() },
                  onClone = { url -> viewModel.gitClone(url) },
                  onTestConcurrencyCheck = { viewModel.testGitConcurrencyCheck() },
                  onToggleAbleFlag = { viewModel.toggleAbleFlag() },
                  concurrencyErrorText = uiState.gitConcurrencyError
                )
                ForgeSection.CONSOLE -> ConsoleScreen(
                  project = uiState.activeProject,
                  history = uiState.consoleHistory,
                  language = uiState.language,
                  onRunCommand = { cmd -> viewModel.runConsoleCommand(cmd) }
                )
                ForgeSection.AI_FIX -> AiFixScreen(
                  project = uiState.activeProject,
                  issues = uiState.diagnosticIssues,
                  activeProposal = uiState.activeProposal,
                  fixStatusMessage = uiState.fixStatusMessage,
                  geminiKeyConfigured = uiState.geminiKeyConfigured,
                  language = uiState.language,
                  onRunDiagnostics = { viewModel.runDiagnostics() },
                  onProposeFix = { issue -> viewModel.proposeAiFix(issue) },
                  onConfirmApplyFix = { proposal -> viewModel.confirmAndApplyAiFix(proposal) },
                  onDismissProposal = { viewModel.dismissAiFixProposal() }
                )
                ForgeSection.DEPLOY -> DeployScreen(
                  project = uiState.activeProject,
                  files = uiState.files,
                  language = uiState.language
                )
                ForgeSection.COMMUNITY -> CommunityScreen(
                  user = uiState.communityUser ?: com.example.core.community.UserProfile(
                    id = "u_me",
                    username = "developer",
                    displayName = "Mobile Developer",
                    bio = "Building accessible apps with ABLE Forge / کارگاه توانا",
                    skills = listOf("Kotlin", "Jetpack Compose", "Accessibility QA"),
                    xpBalance = 120L,
                    referralCode = "ABLE-DEV-701",
                    isVerified = true
                  ),
                  topics = uiState.communityTopics,
                  activeTopic = uiState.activeTopic,
                  messages = uiState.communityMessages,
                  showcases = uiState.communityShowcases,
                  xpEvents = uiState.xpEvents,
                  language = uiState.language,
                  onSelectTopic = { viewModel.selectCommunityTopic(it) },
                  onSendMessage = { viewModel.sendCommunityMessage(it) },
                  onAddSkill = { viewModel.addCommunitySkill(it) },
                  onRemoveSkill = { viewModel.removeCommunitySkill(it) },
                  onBlockUser = { viewModel.blockCommunityUser(it) },
                  onReportMessage = { msgId, reason -> viewModel.reportCommunityMessage(msgId, reason) },
                  onSendOrdinaryGift = { receiver, amt -> viewModel.sendOrdinaryXpGift(receiver, amt) }
                )
                ForgeSection.MONETIZATION -> MonetizationScreen(
                  currentTier = uiState.currentPlanTier,
                  subscriptionExpiry = uiState.subscriptionExpiry,
                  selectedDurationMonths = uiState.selectedDurationMonths,
                  availablePlans = uiState.availablePlans,
                  aiCredits = uiState.aiCredits,
                  coinEconomy = uiState.coinEconomy,
                  paymentIdentity = uiState.paymentIdentity,
                  lastPurchaseResult = uiState.lastPurchaseResult,
                  lastVerificationResult = uiState.lastVerificationResult,
                  restoreMessage = uiState.restoreMessage,
                  language = uiState.language,
                  onSelectDuration = { viewModel.selectMonetizationDuration(it) },
                  onInitiatePurchase = { plan, duration, identity -> viewModel.initiatePurchase(plan, duration, identity) },
                  onPurchaseWithCoins = { tier, duration -> viewModel.purchaseWithCoins(tier, duration) },
                  onConsumeHeavyAi = { viewModel.consumeHeavyAi(it) },
                  onRestorePurchases = { viewModel.restorePurchases() },
                  onUpdateIdentity = { viewModel.updatePaymentIdentity(it) },
                  onSelectCoinUnitName = { en, fa -> viewModel.setCoinUnitName(en, fa) }
                )
                ForgeSection.TUTORIAL -> TutorialScreen(
                  language = uiState.language,
                  onNavigateSection = { viewModel.setSection(it) }
                )
                ForgeSection.HELP -> HelpScreen(
                  language = uiState.language
                )
                ForgeSection.SETTINGS -> SettingsScreen(
                  currentTheme = uiState.themeMode,
                  currentLanguage = uiState.language,
                  reducedMotion = uiState.reducedMotion,
                  fontScale = uiState.fontScale,
                  guidanceState = uiState.guidanceState,
                  onThemeChanged = { viewModel.setThemeMode(it) },
                  onLanguageChanged = { viewModel.setLanguage(it) },
                  onReducedMotionChanged = { viewModel.setReducedMotion(it) },
                  onFontScaleChanged = { viewModel.setFontScale(it) },
                  onOpenGuidancePicker = { viewModel.openManualTierSelection() },
                  onResetGuidanceToAdaptive = { viewModel.chooseAdaptiveGuidance() }
                )
              }
            }
          }
        }
      }
    }
  }
}
