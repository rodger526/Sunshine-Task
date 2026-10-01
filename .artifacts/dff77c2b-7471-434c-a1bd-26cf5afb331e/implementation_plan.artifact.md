# Sunshine Task UI Redesign Implementation Plan

Redesigning the app UI to match the provided reference design screenshots (Dashboard stats with 4 cards, updated filter chips, polished TaskCard, redesigned CreateTaskScreen with DatePicker and priority buttons, and TaskDetailScreen with Evidencias section).

## User Review Required

> [!NOTE]
> - **Dashboard Stats**: 4 cards (Total, Pendientes, Completadas, Vencidas) arranged in a 2x2 grid.
> - **Filters**: "Todas", "Pendientes", "Hechas".
> - **CreateTaskScreen**: Modal dialog layout with distinct colored priority buttons (Baja, Media, Alta, Urgente) and interactive DatePicker.
> - **TaskDetailScreen**: Top action bar with "Volver" and red "Eliminar", status selection buttons, and "Evidencias" section with "+ Adjuntar evidencia".

## Proposed Changes

### Dashboard & Home (`ui/components/StatsSection.kt`, `ui/home/HomeScreen.kt`, `ui/components/FilterSection.kt`)
- Update `StatsSection` to display 4 cards in a 2x2 grid (Total, Pendientes, Completadas, Vencidas).
- Update `FilterSection` filters to `"TODAS" -> "Todas"`, `"PENDIENTE" -> "Pendientes"`, `"HECHAS" -> "Hechas"` (or `"COMPLETADA" -> "Hechas"`).

### Task Creation (`ui/task/CreateTaskScreen.kt`)
- Redesign `CreateTaskScreen` layout to match the reference modal sheet ("Nueva tarea").
- Add interactive `DatePickerDialog` when clicking on "Seleccionar fecha límite".
- Add styled outline priority buttons (Baja, Media, Alta, Urgente).

### Task Detail (`ui/task/TaskDetailScreen.kt`)
- Update top bar with "< Volver" and red "Eliminar".
- Add "Estado" selection buttons (Pendiente, En progreso, Completada).
- Add "Evidencias" section with descriptive text, "+ Adjuntar evidencia" button, and empty state.

## Verification Plan

### Automated Tests
- Run `gradle_build("app:assembleDebug")` to ensure compilation success.

### Manual Verification
- Deploy to device/emulator and verify UI matches screenshots.
