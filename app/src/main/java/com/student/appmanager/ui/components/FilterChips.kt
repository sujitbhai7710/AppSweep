package com.student.appmanager.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.student.appmanager.data.model.FilterOption
import com.student.appmanager.ui.theme.*

/**
 * Horizontal scrollable row of filter chips.
 *
 * Design:
 * - Horizontally scrollable chip row
 * - Selected chip has blue background with white text
 * - Unselected chips have light gray background
 * - Rounded pill-shaped chips with smooth selection animation
 *
 * Filters available:
 * - All Apps (default)
 * - User Apps Only
 * - System Apps Only
 * - Recently Installed (last 7 days)
 * - Largest Apps
 *
 * @param selectedFilter Currently selected filter
 * @param onFilterSelected Called when a filter chip is tapped
 * @param modifier Optional modifier
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterChipsRow(
    selectedFilter: FilterOption,
    onFilterSelected: (FilterOption) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterOption.values().forEach { filter ->
            val isSelected = filter == selectedFilter

            FilterChip(
                selected = isSelected,
                onClick = { onFilterSelected(filter) },
                label = {
                    Text(
                        text = filter.displayName,
                        style = MaterialTheme.typography.labelMedium
                    )
                },
                shape = RoundedCornerShape(20.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Blue500,
                    selectedLabelColor = White,
                    containerColor = Gray100,
                    labelColor = Gray600
                ),
                border = FilterChipDefaults.filterChipBorder(
                    borderColor = if (isSelected) Blue500 else Gray300,
                    selectedBorderColor = Blue500,
                    selected = isSelected
                )
            )
        }
    }
}

/**
 * Sort option dropdown menu.
 *
 * Design:
 * - Compact button showing current sort option
 * - Dropdown with all available sort options
 * - Checkmark next to currently selected option
 * - Blue accent for selected items
 *
 * Sort options:
 * - Name A-Z (default)
 * - Name Z-A
 * - Size (Largest first)
 * - Size (Smallest first)
 * - Install Date (Newest)
 * - Install Date (Oldest)
 *
 * @param selectedSort Currently selected sort option
 * @param onSortSelected Called when a sort option is chosen
 * @param isExpanded Whether the dropdown is currently open
 * @param onExpandChange Called when dropdown visibility changes
 */
@Composable
fun SortDropdown(
    selectedSort: com.student.appmanager.data.model.SortOption,
    onSortSelected: (com.student.appmanager.data.model.SortOption) -> Unit,
    isExpanded: Boolean,
    onExpandChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        OutlinedButton(
            onClick = { onExpandChange(true) },
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = White,
                contentColor = Gray800
            ),
            border = ButtonDefaults.outlinedButtonBorder,
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Sort,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = selectedSort.displayName,
                style = MaterialTheme.typography.labelMedium
            )
        }

        DropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { onExpandChange(false) }
        ) {
            com.student.appmanager.data.model.SortOption.values().forEach { sort ->
                DropdownMenuItem(
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = sort.displayName,
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (sort == selectedSort) Blue500 else Gray800
                            )
                        }
                    },
                    onClick = {
                        onSortSelected(sort)
                        onExpandChange(false)
                    },
                    leadingIcon = {
                        if (sort == selectedSort) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = null,
                                tint = Blue500,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                )
            }
        }
    }
}
