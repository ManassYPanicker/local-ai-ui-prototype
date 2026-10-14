package com.example.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.DemoDataProvider
import com.example.model.Conversation
import com.example.model.LocalModel
import com.example.model.PairedComputer
import com.example.model.PcConnectionStatus
import com.example.ui.screens.AppThemeMode
import com.example.ui.screens.ChatScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.PairingScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.Indigo500
import com.example.ui.theme.LocalAiTheme

enum class Screen {
  HOME,
  CHAT,
  HISTORY,
  PAIRING,
  SETTINGS
}

@Composable
fun LocalAiApp() {
  var themeMode by remember { mutableStateOf(AppThemeMode.SYSTEM) }
  val systemDark = isSystemInDarkTheme()
  val isDark = when (themeMode) {
    AppThemeMode.SYSTEM -> systemDark
    AppThemeMode.LIGHT -> false
    AppThemeMode.DARK -> true
  }

  LocalAiTheme(darkTheme = isDark) {
    var currentScreen by remember { mutableStateOf(Screen.HOME) }
    var selectedConversation by remember { mutableStateOf<Conversation?>(null) }

    // Prototype demonstration connection status
    var connectionStatus by remember { mutableStateOf(PcConnectionStatus.CONNECTED) }
    val computerList = remember { mutableStateListOf<PairedComputer>().apply { addAll(DemoDataProvider.sampleComputers) } }
    var activeComputer by remember { mutableStateOf<PairedComputer?>(computerList.firstOrNull()) }

    val modelList = remember { mutableStateListOf<LocalModel>().apply { addAll(DemoDataProvider.sampleModels) } }
    val conversationsList = remember { mutableStateListOf<Conversation>().apply { addAll(DemoDataProvider.getInitialConversations()) } }

    fun toggleDemoState() {
      connectionStatus = when (connectionStatus) {
        PcConnectionStatus.CONNECTED -> PcConnectionStatus.DISCONNECTED
        PcConnectionStatus.DISCONNECTED -> PcConnectionStatus.NOT_PAIRED
        PcConnectionStatus.NOT_PAIRED -> PcConnectionStatus.CONNECTED
        PcConnectionStatus.CONNECTING -> PcConnectionStatus.CONNECTED
      }
    }

    val isImmersiveScreen = currentScreen == Screen.CHAT || currentScreen == Screen.PAIRING

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
      val isExpandedScreen = maxWidth >= 600.dp

      if (isExpandedScreen) {
        // Wide Tablet / Desktop Layout: Navigation Rail on the Left
        Row(modifier = Modifier.fillMaxSize()) {
          if (!isImmersiveScreen) {
            NavigationRail(
              modifier = Modifier.fillMaxHeight(),
              containerColor = MaterialTheme.colorScheme.surface,
              header = {
                FloatingActionButton(
                  onClick = {
                    selectedConversation = null
                    currentScreen = Screen.CHAT
                  },
                  containerColor = Indigo500,
                  contentColor = Color.White,
                  modifier = Modifier.testTag("rail_new_chat_fab")
                ) {
                  Icon(Icons.Default.Add, contentDescription = "New Chat")
                }
              }
            ) {
              NavigationRailItem(
                selected = currentScreen == Screen.HOME,
                onClick = { currentScreen = Screen.HOME },
                icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                label = { Text("Home") },
                colors = NavigationRailItemDefaults.colors(
                  selectedIconColor = Indigo500,
                  indicatorColor = Indigo500.copy(alpha = 0.15f)
                )
              )
              NavigationRailItem(
                selected = currentScreen == Screen.HISTORY,
                onClick = { currentScreen = Screen.HISTORY },
                icon = { Icon(Icons.Default.ChatBubbleOutline, contentDescription = "Sessions") },
                label = { Text("Sessions") },
                colors = NavigationRailItemDefaults.colors(
                  selectedIconColor = Indigo500,
                  indicatorColor = Indigo500.copy(alpha = 0.15f)
                )
              )
              NavigationRailItem(
                selected = currentScreen == Screen.SETTINGS,
                onClick = { currentScreen = Screen.SETTINGS },
                icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                label = { Text("Settings") },
                colors = NavigationRailItemDefaults.colors(
                  selectedIconColor = Indigo500,
                  indicatorColor = Indigo500.copy(alpha = 0.15f)
                )
              )
            }
          }

          Box(
            modifier = Modifier
              .weight(1f)
              .fillMaxHeight(),
            contentAlignment = Alignment.TopCenter
          ) {
            Box(
              modifier = Modifier
                .widthIn(max = 800.dp)
                .fillMaxSize()
            ) {
              AppScreenContent(
                currentScreen = currentScreen,
                selectedConversation = selectedConversation,
                connectionStatus = connectionStatus,
                activeComputer = activeComputer,
                conversationsList = conversationsList,
                modelList = modelList,
                themeMode = themeMode,
                onThemeModeChange = { themeMode = it },
                onToggleDemoState = { toggleDemoState() },
                onNavigate = { screen, conv ->
                  selectedConversation = conv
                  currentScreen = screen
                },
                onPairingComplete = { comp ->
                  connectionStatus = PcConnectionStatus.CONNECTED
                  activeComputer = comp
                  currentScreen = Screen.HOME
                }
              )
            }
          }
        }
      } else {
        // Mobile Compact Layout: Scaffold with Navigation Bar
        Scaffold(
          bottomBar = {
            if (!isImmersiveScreen) {
              NavigationBar(
                modifier = Modifier.navigationBarsPadding(),
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 2.dp
              ) {
                NavigationBarItem(
                  selected = currentScreen == Screen.HOME,
                  onClick = { currentScreen = Screen.HOME },
                  icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                  label = { Text("Home") },
                  colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Indigo500,
                    indicatorColor = Indigo500.copy(alpha = 0.15f)
                  ),
                  modifier = Modifier.testTag("nav_home")
                )
                NavigationBarItem(
                  selected = currentScreen == Screen.HISTORY,
                  onClick = { currentScreen = Screen.HISTORY },
                  icon = { Icon(Icons.Default.ChatBubbleOutline, contentDescription = "History") },
                  label = { Text("Chats") },
                  colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Indigo500,
                    indicatorColor = Indigo500.copy(alpha = 0.15f)
                  ),
                  modifier = Modifier.testTag("nav_history")
                )
                NavigationBarItem(
                  selected = currentScreen == Screen.SETTINGS,
                  onClick = { currentScreen = Screen.SETTINGS },
                  icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                  label = { Text("Settings") },
                  colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Indigo500,
                    indicatorColor = Indigo500.copy(alpha = 0.15f)
                  ),
                  modifier = Modifier.testTag("nav_settings")
                )
              }
            }
          },
          modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
          Box(
            modifier = Modifier
              .fillMaxSize()
              .navigationBarsPadding()
          ) {
            AppScreenContent(
              currentScreen = currentScreen,
              selectedConversation = selectedConversation,
              connectionStatus = connectionStatus,
              activeComputer = activeComputer,
              conversationsList = conversationsList,
              modelList = modelList,
              themeMode = themeMode,
              onThemeModeChange = { themeMode = it },
              onToggleDemoState = { toggleDemoState() },
              onNavigate = { screen, conv ->
                selectedConversation = conv
                currentScreen = screen
              },
              onPairingComplete = { comp ->
                connectionStatus = PcConnectionStatus.CONNECTED
                activeComputer = comp
                currentScreen = Screen.HOME
              }
            )
          }
        }
      }
    }
  }
}

@Composable
private fun AppScreenContent(
  currentScreen: Screen,
  selectedConversation: Conversation?,
  connectionStatus: PcConnectionStatus,
  activeComputer: PairedComputer?,
  conversationsList: List<Conversation>,
  modelList: List<LocalModel>,
  themeMode: AppThemeMode,
  onThemeModeChange: (AppThemeMode) -> Unit,
  onToggleDemoState: () -> Unit,
  onNavigate: (Screen, Conversation?) -> Unit,
  onPairingComplete: (PairedComputer) -> Unit
) {
  AnimatedContent(
    targetState = currentScreen,
    transitionSpec = { fadeIn() togetherWith fadeOut() },
    label = "screenTransitions"
  ) { screen ->
    when (screen) {
      Screen.HOME -> {
        HomeScreen(
          connectionStatus = connectionStatus,
          currentComputer = activeComputer,
          recentConversations = conversationsList,
          onToggleDemoState = onToggleDemoState,
          onNewChat = { onNavigate(Screen.CHAT, null) },
          onOpenConversation = { conv -> onNavigate(Screen.CHAT, conv) },
          onNavigateToHistory = { onNavigate(Screen.HISTORY, null) },
          onNavigateToPairing = { onNavigate(Screen.PAIRING, null) },
          onNavigateToSettings = { onNavigate(Screen.SETTINGS, null) }
        )
      }
      Screen.CHAT -> {
        ChatScreen(
          conversation = selectedConversation,
          availableModels = modelList,
          onBackClick = { onNavigate(Screen.HOME, null) }
        )
      }
      Screen.HISTORY -> {
        HistoryScreen(
          conversations = conversationsList,
          onOpenConversation = { conv -> onNavigate(Screen.CHAT, conv) },
          onBackClick = { onNavigate(Screen.HOME, null) },
          onNewChat = { onNavigate(Screen.CHAT, null) }
        )
      }
      Screen.PAIRING -> {
        PairingScreen(
          onBackClick = { onNavigate(Screen.HOME, null) },
          onPairingComplete = onPairingComplete
        )
      }
      Screen.SETTINGS -> {
        SettingsScreen(
          currentThemeMode = themeMode,
          onThemeModeChange = onThemeModeChange,
          onBackClick = { onNavigate(Screen.HOME, null) },
          onNavigateToPairing = { onNavigate(Screen.PAIRING, null) }
        )
      }
    }
  }
}
