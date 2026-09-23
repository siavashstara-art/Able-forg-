package com.example.features.community

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.community.ChatMessage
import com.example.core.community.CollaborationRole
import com.example.core.community.CommunityFeatureFlags
import com.example.core.community.CommunityTopic
import com.example.core.community.ProjectShowcase
import com.example.core.community.ServerAuthoritativeXpLedger
import com.example.core.community.UserProfile
import com.example.core.community.XpEvent
import com.example.core.localization.ForgeLanguage
import com.example.core.localization.ForgeStrings
import com.example.ui.components.TrafficSignalType
import com.example.ui.components.UniversalTrafficPill

@Composable
fun CommunityScreen(
  user: UserProfile,
  topics: List<CommunityTopic>,
  activeTopic: CommunityTopic,
  messages: List<ChatMessage>,
  showcases: List<ProjectShowcase>,
  xpEvents: List<XpEvent>,
  language: ForgeLanguage,
  onSelectTopic: (CommunityTopic) -> Unit,
  onSendMessage: (String) -> Unit,
  onAddSkill: (String) -> Unit,
  onRemoveSkill: (String) -> Unit,
  onBlockUser: (String) -> Unit,
  onReportMessage: (String, String) -> Unit,
  onSendOrdinaryGift: (receiverId: String, amount: Long) -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedTab by remember { mutableIntStateOf(0) }
  var newSkillInput by remember { mutableStateOf("") }
  var chatInput by remember { mutableStateOf("") }
  var giftRecipientId by remember { mutableStateOf("u_sara") }
  var giftAmountText by remember { mutableStateOf("15") }

  Column(modifier = modifier.fillMaxSize()) {
    // Top Tab Row
    TabRow(
      selectedTabIndex = selectedTab,
      containerColor = MaterialTheme.colorScheme.surfaceVariant,
      contentColor = MaterialTheme.colorScheme.primary
    ) {
      Tab(
        selected = selectedTab == 0,
        onClick = { selectedTab = 0 },
        text = { Text("Profile", fontWeight = FontWeight.Bold, fontSize = 13.sp) }
      )
      Tab(
        selected = selectedTab == 1,
        onClick = { selectedTab = 1 },
        text = { Text("Rooms", fontWeight = FontWeight.Bold, fontSize = 13.sp) }
      )
      Tab(
        selected = selectedTab == 2,
        onClick = { selectedTab = 2 },
        text = { Text("Collab", fontWeight = FontWeight.Bold, fontSize = 13.sp) }
      )
      Tab(
        selected = selectedTab == 3,
        onClick = { selectedTab = 3 },
        text = { Text("XP & Leagues", fontWeight = FontWeight.Bold, fontSize = 13.sp) }
      )
    }

    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // TAB 0: PROFILE & SKILLS
      if (selectedTab == 0) {
        item {
          Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = RoundedCornerShape(12.dp)
          ) {
            Column(modifier = Modifier.padding(16.dp)) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                      .size(48.dp)
                      .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
                  ) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                  }
                  Spacer(modifier = Modifier.width(12.dp))
                  Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Text(user.displayName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                      if (user.isVerified) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Default.Verified, contentDescription = "Verified", tint = Color(0xFF38BDF8), modifier = Modifier.size(16.dp))
                      }
                    }
                    Text("@${user.username}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                  }
                }

                Surface(
                  color = MaterialTheme.colorScheme.primaryContainer,
                  shape = RoundedCornerShape(8.dp)
                ) {
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                  ) {
                    Icon(Icons.Default.Stars, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("${user.xpBalance} XP", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                  }
                }
              }

              Spacer(modifier = Modifier.height(10.dp))
              Text(user.bio, style = MaterialTheme.typography.bodyMedium)

              Spacer(modifier = Modifier.height(10.dp))
              Surface(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(6.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  modifier = Modifier.padding(8.dp)
                ) {
                  Text("Referral Code: ", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                  Text(user.referralCode, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                }
              }
            }
          }
        }

        // Skills Registration
        item {
          Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
            shape = RoundedCornerShape(12.dp)
          ) {
            Column(modifier = Modifier.padding(16.dp)) {
              Text("Registered Developer Skills", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
              Spacer(modifier = Modifier.height(8.dp))

              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                user.skills.forEach { skill ->
                  Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(16.dp)
                  ) {
                    Row(
                      verticalAlignment = Alignment.CenterVertically,
                      modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                      Text(skill, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                      Spacer(modifier = Modifier.width(4.dp))
                      Icon(
                        Icons.Default.Close,
                        contentDescription = "Remove $skill",
                        modifier = Modifier
                          .size(14.dp)
                          .clickable { onRemoveSkill(skill) }
                      )
                    }
                  }
                }
              }

              Spacer(modifier = Modifier.height(12.dp))

              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                OutlinedTextField(
                  value = newSkillInput,
                  onValueChange = { newSkillInput = it },
                  placeholder = { Text("Add skill (e.g. Accessibility QA)...") },
                  modifier = Modifier.weight(1f)
                )
                Button(
                  onClick = {
                    if (newSkillInput.isNotBlank()) {
                      onAddSkill(newSkillInput)
                      newSkillInput = ""
                    }
                  },
                  shape = RoundedCornerShape(8.dp)
                ) {
                  Icon(Icons.Default.Add, contentDescription = null)
                }
              }
            }
          }
        }
      }

      // TAB 1: TOPIC ROOMS & DISCUSSION
      if (selectedTab == 1) {
        // Topics Horizontal Selector
        item {
          Text("Community Topic Rooms (11 Active Channels)", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
          Spacer(modifier = Modifier.height(6.dp))
          LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            items(topics) { topic ->
              FilterChip(
                selected = topic.id == activeTopic.id,
                onClick = { onSelectTopic(topic) },
                label = { Text("${topic.iconLabel} ${topic.titleEn}") }
              )
            }
          }
        }

        // Active Topic Chat Feed
        item {
          Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
            shape = RoundedCornerShape(12.dp)
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
              ) {
                Text(
                  text = "${activeTopic.iconLabel} #${activeTopic.titleEn} Discussion",
                  fontWeight = FontWeight.Bold,
                  style = MaterialTheme.typography.titleMedium
                )
                UniversalTrafficPill(signal = TrafficSignalType.READY, label = "ANTI-SPAM ACTIVE")
              }

              Spacer(modifier = Modifier.height(10.dp))

              val topicMessages = messages.filter { it.topicId == activeTopic.id }
              if (topicMessages.isEmpty()) {
                Text(
                  "No messages in this topic room yet. Start the conversation!",
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              } else {
                topicMessages.forEach { msg ->
                  Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                      .fillMaxWidth()
                      .padding(vertical = 4.dp)
                  ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                      Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                      ) {
                        Text(msg.senderName, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                        Row {
                          IconButton(
                            onClick = { onReportMessage(msg.id, "Inappropriate content") },
                            modifier = Modifier.size(24.dp)
                          ) {
                            Icon(Icons.Default.Report, contentDescription = "Report", tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(16.dp))
                          }
                          IconButton(
                            onClick = { onBlockUser(msg.senderId) },
                            modifier = Modifier.size(24.dp)
                          ) {
                            Icon(Icons.Default.Block, contentDescription = "Block", tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(16.dp))
                          }
                        }
                      }
                      Spacer(modifier = Modifier.height(2.dp))
                      Text(msg.content, style = MaterialTheme.typography.bodyMedium)
                    }
                  }
                }
              }

              Spacer(modifier = Modifier.height(12.dp))

              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                OutlinedTextField(
                  value = chatInput,
                  onValueChange = { chatInput = it },
                  placeholder = { Text("Message #${activeTopic.titleEn}...") },
                  modifier = Modifier.weight(1f)
                )
                Button(
                  onClick = {
                    if (chatInput.isNotBlank()) {
                      onSendMessage(chatInput)
                      chatInput = ""
                    }
                  },
                  shape = RoundedCornerShape(8.dp)
                ) {
                  Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send")
                }
              }
            }
          }
        }
      }

      // TAB 2: COLLABORATION & SHOWCASE
      if (selectedTab == 2) {
        item {
          Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = RoundedCornerShape(12.dp)
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Handshake, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Collaboration Opportunities by Role", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
              }
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                "Standard Roles: Software Developer, UI/UX Designer, Translator & Localizer, QA Tester, Accessibility QA Specialist.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }

        items(showcases) { show ->
          Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
            shape = RoundedCornerShape(12.dp)
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
              ) {
                Text(show.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                if (show.hasAbleFlag) {
                  Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(6.dp)
                  ) {
                    Row(
                      verticalAlignment = Alignment.CenterVertically,
                      modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                      Icon(Icons.Default.Flag, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(12.dp))
                      Spacer(modifier = Modifier.width(4.dp))
                      Text("ABLE FLAG", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                  }
                }
              }

              Spacer(modifier = Modifier.height(4.dp))
              Text(show.description, style = MaterialTheme.typography.bodyMedium)

              Spacer(modifier = Modifier.height(8.dp))
              Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
              ) {
                show.neededRoles.forEach { role ->
                  Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(6.dp)
                  ) {
                    Text(
                      role.titleEn,
                      fontSize = 11.sp,
                      color = MaterialTheme.colorScheme.onSecondaryContainer,
                      modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                  }
                }
              }
            }
          }
        }
      }

      // TAB 3: XP SYSTEM, ABLE FLAG, LEAGUES & DISCLAIMER
      if (selectedTab == 3) {
        // Ordinary XP Gift Rule Card
        item {
          Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
            shape = RoundedCornerShape(12.dp)
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Stars, contentDescription = null, tint = Color(0xFFF59E0B))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Ordinary XP Gifting Rules", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
              }
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                "• Maximum 30% cap of your available XP pool per gift.\n• Frequency: At most once per month for the giver.\n• Anti-Farming: Max once in 6 months to the same recipient.\n• Does NOT require ABLE Flag.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )

              Spacer(modifier = Modifier.height(10.dp))

              Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                  value = giftAmountText,
                  onValueChange = { giftAmountText = it },
                  label = { Text("Amount (XP)") },
                  modifier = Modifier.weight(1f)
                )
                Button(
                  onClick = {
                    val amt = giftAmountText.toLongOrNull() ?: 10L
                    onSendOrdinaryGift(giftRecipientId, amt)
                  },
                  shape = RoundedCornerShape(8.dp),
                  modifier = Modifier.align(Alignment.CenterVertically)
                ) {
                  Text("Send Gift (30% Cap)")
                }
              }
            }
          }
        }

        // Collaboration XP & ABLE Flag Card
        item {
          Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = RoundedCornerShape(12.dp)
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Flag, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Collaboration XP & ABLE Flag Rules", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
              }

              Spacer(modifier = Modifier.height(8.dp))

              Text(
                "• Higher Collaboration XP is strictly reserved for projects deployed under the ABLE Flag.\n• Tier Caps based on team size:\n   - 2 Collaborators: 60% cap\n   - 3 Collaborators: 70% cap\n   - 4+ Collaborators: 80% cap\n• Real collaboration verification required (nominal or fake collaborator additions are rejected).",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }

        // Leagues: Value Creators & Introducers
        item {
          Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
            shape = RoundedCornerShape(12.dp)
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = Color(0xFFF59E0B))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Ecosystem Leagues", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
              }
              Spacer(modifier = Modifier.height(8.dp))
              Text(
                "1. League of Value Creators: Records eligible economic and qualifying purchasing activity.\n2. League of Introducers: Tracks referral code attribution and valid community onboarding.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }

        // MANDATORY LEGAL DISCLAIMER
        item {
          Surface(
            color = Color(0xFFF59E0B).copy(alpha = 0.12f),
            border = BorderStroke(1.dp, Color(0xFFF59E0B).copy(alpha = 0.4f)),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              verticalAlignment = Alignment.Top,
              modifier = Modifier.padding(12.dp)
            ) {
              Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(20.dp))
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text("Mandatory Participation Notice", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFFF59E0B))
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                  ServerAuthoritativeXpLedger.NON_GUARANTEE_DISCLAIMER,
                  style = MaterialTheme.typography.bodySmall,
                  fontSize = 11.sp,
                  color = MaterialTheme.colorScheme.onSurface
                )
              }
            }
          }
        }

        // Architectural Contracts for Disabled Future Systems
        item {
          Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
            shape = RoundedCornerShape(10.dp)
          ) {
            Column(modifier = Modifier.padding(12.dp)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Future Modules (Contracts Ready • Disabled in v0.1)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
              }
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                "• Group System (Uniform role vocabulary: Member, Contributor, Coordinator, Maintainer) — DISABLED.\n• Future Recognitions (Titles, Hall of Fame, Legacy) — DISABLED.",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }
      }
    }
  }
}
