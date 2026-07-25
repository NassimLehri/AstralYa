# Walkthrough - Epic 16: Living NPCs & Autonomous Routines

I have transformed the static world of AstralYa into a living environment by implementing an autonomous NPC AI system, allowing characters to follow routines and react to the player's presence.

## Key AI Enhancements

### 🏃 1. Autonomous NPC Routines
- **Task-Based AI**: Introduced `NPCTask` and `NPCRoutine` models. NPCs can now perform a series of actions:
    - **WAIT**: The NPC stays at a position (e.g., working at a stall).
    - **MOVE_TO**: The NPC walks toward specific coordinates at a realistic speed.
- **Dynamic Instances**: Created `NPCInstance` to manage the real-time state (position, direction, current task) of each character separately from the map data.
- **Village Life**: The **Marchande Selya** now actively walks between her counter and her supplies, making the marketplace feel occupied.

### 🧠 2. Reactive Behavior Tree Logic
- **Pause on Interaction**: Implemented a "Interaction Lock". When you talk to an NPC, they immediately stop their routine and face you, resuming their life only after the conversation ends.
- **Directional Awareness**: NPCs now correctly update their sprite direction based on their movement vector, using the same professional animation engine as the player.

### 😊 3. Emotional State & Mood
- **Mood System**: NPCs now possess a `mood` property (e.g., NEUTRAL, HAPPY).
- **Consistency**: This lays the foundation for dynamic dialogue variations and future "emote bubbles" above their heads.

## Verification Results

### Automated Tests
- Updated the core engine suite to handle the new dynamic NPC instances.
- **Status**: SUCCESS (All 52 project tests passed).

### Manual Verification Path
1. **Observe the Market**: Stand still near Selya in the Village. You will see her wait for 3 seconds, walk to the right, wait again, and return to her original spot.
2. **Interrupt the Routine**: Walk up to her while she is moving and press the Action button.
    - Verify she stops moving immediately.
    - Complete the dialogue and verify she resumes her walk toward her target.
3. **Mini-map Tracking**: Watch the yellow dots on the mini-map. They now move in real-time as the NPCs walk through the village.
