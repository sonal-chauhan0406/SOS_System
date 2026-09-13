package com.example.okdriverpanicbutton.data

import kotlin.random.Random

/**
 * Generates a list of mock users with procedurally-generated names,
 * randomized coordinates, online statuses, and avatar emojis.
 * Uses a seeded random for session reproducibility.
 */
class MockUserRepository(seed: Long = System.currentTimeMillis()) {

    private val random = Random(seed)

    private val adjectives = listOf(
        "Swift", "Brave", "Silent", "Bright", "Lucky",
        "Bold", "Calm", "Fierce", "Gentle", "Iron",
        "Keen", "Noble", "Quick", "Sharp", "Vivid",
        "Rapid", "Steady", "Agile", "Daring", "Mighty"
    )

    private val nouns = listOf(
        "Eagle", "Tiger", "Falcon", "Wolf", "Panther",
        "Hawk", "Bear", "Fox", "Lynx", "Viper",
        "Raven", "Cobra", "Jaguar", "Puma", "Sparrow",
        "Otter", "Crane", "Bison", "Stag", "Drake"
    )

    private val emojis = listOf(
        "🦅", "🐯", "🦊", "🐺", "🐻",
        "🦁", "🐱", "🐶", "🦉", "🐸",
        "🦇", "🐼", "🐨", "🦄", "🐙"
    )

    fun getUsers(count: Int = 15): List<MockUser> {
        val usedNames = mutableSetOf<String>()
        return (1..count).map { id ->
            var name: String
            do {
                val adj = adjectives[random.nextInt(adjectives.size)]
                val noun = nouns[random.nextInt(nouns.size)]
                name = "$adj $noun"
            } while (name in usedNames)
            usedNames.add(name)

            MockUser(
                id = id,
                name = name,
                x = random.nextDouble(0.0, 100.0),
                y = random.nextDouble(0.0, 100.0),
                isOnline = random.nextFloat() < 0.7f, // ~70% chance online
                avatarEmoji = emojis[random.nextInt(emojis.size)]
            )
        }
    }
}
