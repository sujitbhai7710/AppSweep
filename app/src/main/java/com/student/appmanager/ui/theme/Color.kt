package com.student.appmanager.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * AppSweep Color Palette - Light, Fresh, and Attractive
 *
 * Design Philosophy:
 * - Primary: Vibrant blue-purple gradient feel for a modern tech aesthetic
 * - Secondary: Warm accent for call-to-action elements
 * - Surface: Clean whites and soft grays for readability
 * - Error: Clear red for destructive actions (uninstall)
 * - Success: Green for positive feedback
 *
 * The palette is inspired by modern fintech and productivity apps
 * that use clean, bright colors with subtle depth.
 */

// ---- Primary Colors (Main brand identity) ----
val Blue500 = Color(0xFF3B82F6)       // Main primary - vibrant blue
val Blue600 = Color(0xFF2563EB)       // Darker primary for pressed states
val Blue700 = Color(0xFF1D4ED8)       // Deepest primary for status bars
val Blue100 = Color(0xFFDBEAFE)       // Light primary for backgrounds
val Blue50 = Color(0xFFEFF6FF)        // Lightest primary for subtle surfaces

// ---- Secondary Colors (Accent & highlights) ----
val Violet500 = Color(0xFF8B5CF6)     // Accent purple
val Violet100 = Color(0xFFEDE9FE)     // Light purple background
val Orange500 = Color(0xFFF97316)     // Warm accent for CTAs
val Orange100 = Color(0xFFFFF7ED)     // Light orange background

// ---- Semantic Colors ----
val Green500 = Color(0xFF22C55E)      // Success / positive actions
val Green100 = Color(0xFFDCFCE7)      // Light green background
val Red500 = Color(0xFFEF4444)        // Error / destructive actions
val Red100 = Color(0xFFFEE2E2)        // Light red background
val Yellow500 = Color(0xFFEAB308)     // Warning
val Yellow100 = Color(0xFFFEF9C3)     // Light warning background

// ---- Neutral Colors (Text & surfaces) ----
val Gray900 = Color(0xFF111827)       // Primary text
val Gray800 = Color(0xFF1F2937)       // Secondary text
val Gray600 = Color(0xFF4B5563)       // Tertiary text / hints
val Gray400 = Color(0xFF9CA3AF)       // Disabled / dividers
val Gray300 = Color(0xFFD1D5DB)       // Borders
val Gray200 = Color(0xFFE5E7EB)       // Light borders
val Gray100 = Color(0xFFF3F4F6)       // Surface backgrounds
val Gray50 = Color(0xFFF9FAFB)        // Subtle surface tint
val White = Color(0xFFFFFFFF)          // Pure white surface

// ---- Gradient Colors ----
val GradientStart = Color(0xFF3B82F6) // Blue
val GradientEnd = Color(0xFF8B5CF6)   // Purple

// ---- Category Chip Colors ----
val ChipUserBg = Color(0xFFDBEAFE)    // Light blue for user apps
val ChipUserText = Color(0xFF1D4ED8)  // Dark blue text
val ChipSystemBg = Color(0xFFEDE9FE)  // Light purple for system apps
val ChipSystemText = Color(0xFF6D28D9) // Dark purple text
val ChipAllBg = Color(0xFFF3F4F6)     // Light gray for all
val ChipAllText = Color(0xFF374151)   // Dark gray text
