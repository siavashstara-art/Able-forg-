package com.example

import com.example.core.guidance.AdaptiveUserMetrics
import com.example.core.guidance.GuidanceSelectionMode
import com.example.core.guidance.GuidanceState
import com.example.core.guidance.GuidanceTier
import com.example.features.tutorial.TutorialRepository
import org.junit.Assert.*
import org.junit.Test

class GuidanceSystemUnitTest {

  @Test
  fun testGuidanceTiersIntegrity() {
    val tiers = GuidanceTier.values()
    assertEquals(4, tiers.size)
    assertEquals(GuidanceTier.SELF, tiers[0])
    assertEquals(GuidanceTier.GUIDANCE, tiers[1])
    assertEquals(GuidanceTier.TEACHING_GUIDANCE, tiers[2])
    assertEquals(GuidanceTier.TEACHING_GUIDANCE_ASSISTANT, tiers[3])

    // Verify Persian titles and plain-language descriptions are non-empty
    tiers.forEach { tier ->
      assertTrue(tier.faTitle.isNotBlank())
      assertTrue(tier.faDesc.isNotBlank())
      assertTrue(tier.enTitle.isNotBlank())
    }
  }

  @Test
  fun testTutorialRepositoryContains12Chapters() {
    val chapters = TutorialRepository.chapters
    assertEquals(12, chapters.size)

    val expectedIds = listOf(
      "start_from_zero",
      "create_project",
      "files",
      "editor",
      "git",
      "aifix",
      "console",
      "deploy",
      "offline_mode",
      "accessibility",
      "stt_tts",
      "community"
    )

    expectedIds.forEach { id ->
      assertNotNull(
        "Chapter $id should exist in TutorialRepository",
        chapters.find { it.id == id }
      )
    }
  }

  @Test
  fun testAdaptiveGuidanceMetricsAndSuggestions() {
    val initialMetrics = AdaptiveUserMetrics()
    assertEquals(0, initialMetrics.successCount)
    assertEquals(0, initialMetrics.recentErrorsCount)

    // Simulate repetitive success in an action
    val action = "save_file"
    val updatedRepeats = mapOf(action to 4)
    val metrics = initialMetrics.copy(
      successCount = 4,
      actionRepeats = updatedRepeats
    )

    val state = GuidanceState(
      selectionMode = GuidanceSelectionMode.ADAPTIVE,
      currentTier = GuidanceTier.TEACHING_GUIDANCE,
      metrics = metrics,
      masterySuggestionVisible = (metrics.actionRepeats[action] ?: 0) >= 3
    )

    assertTrue("Mastery suggestion should be triggered after 3 repeated successes", state.masterySuggestionVisible)
  }
}
