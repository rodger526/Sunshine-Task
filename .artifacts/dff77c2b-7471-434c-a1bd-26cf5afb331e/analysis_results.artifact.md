# Evaluación de Cumplimiento - Rúbrica de Entregable (Sunshine Task)

Este documento analiza el estado actual del proyecto `Sunshine-Task` frente a cada uno de los puntos exigidos por el docente en la rúbrica del entregable.

## 1. Capa de Interfaz de Usuario (UI) y Navegación
- **Composición y Diseño (Material 3)**: ✅ **Cumple**. La app está construida 100% en Jetpack Compose utilizando Material Design 3, tarjetas, fichas, barras de búsqueda y paleta de colores coherente.
- **Navegación (`NavHost` y `NavController`)**: ❌ **No Cumple**. Actualmente manejamos la navegación de manera manual mediante un estado sellado (`Screen`) en `MainActivity`. **Acción requerida**: Migrar a `androidx.navigation.compose` (`NavHost`, `NavController` y paso de argumentos).
- **State Hoisting (Stateful vs. Stateless)**: ⚠️ **Parcial**. Algunas pantallas manejan estados internos, pero debemos asegurar la regla estricta de que ningún composable secundario reciba la instancia del `ViewModel`.
- **`collectAsStateWithLifecycle()`**: ❌ **No Cumple**. Actualmente los ViewModels exponen propiedades con `mutableStateOf` directas. **Acción requerida**: Migrar a `StateFlow` y consumir con `collectAsStateWithLifecycle()`.

## 2. Capa de Presentación y Estado (MVVM & UiState)
- **Patrón MVVM**: ✅ **Cumple conceptualmente**.
- **Gestión de Estado (`StateFlow` y `UiState`)**: ❌ **No Cumple**. Actualmente se usa estado mutable plano en el ViewModel en lugar de un flujo reactivo `StateFlow<UiState>` con estados de `Loading`, `Success` y `Error`. **Acción requerida**: Implementar una clase sellada `UiState` y `StateFlow`.

## 3. Capa de Datos (Offline-First con Room)
- **Persistencia Local (Room)**: ❌ **No Cumple**. El repositorio (`TaskRepository.kt`) está vacío y los datos están en memoria estática en el ViewModel. **Acción requerida**: Implementar `TaskEntity`, `TaskDao` con `Flow<T>` y `suspend fun`, `TaskDatabase` y `TaskRepositoryImpl`.

## 4. Gestión de Dependencias
- **Inyección de Dependencias (Hilt o AppContainer)**: ❌ **No Cumple**. Actualmente el ViewModel instancia sus listas en memoria directamente. **Acción requerida**: Implementar Hilt (`@HiltViewModel`, `@Inject`, `@Database` modules) o un contenedor manual (`AppContainer`).

## 5. Criterios Opcionales (Puntos Extra)
- Iconografía vectorial adaptable, diseño responsivo, casos de uso (UseCases).

---

## 🚀 Plan de Acción Recomendado para el 10/10

Para cumplir al 100% con la rúbrica del docente antes de la presentación de 5 minutos, debemos implementar:
1. **Base de Datos Room**: Entidad `TaskEntity`, `TaskDao` (con `Flow` y `suspend fun`), Base de datos y Repositorio.
2. **StateFlow y UiState en el ViewModel**: Exponer estado reactivo y eventos de UI.
3. **Jetpack Navigation (`NavHost`)**: Reemplazar el enrutamiento manual por `NavHost` y `NavController`.
4. **Inyección de Dependencias**: Configurar Hilt o un contenedor manual limpio para proveer la base de datos y repositorio al ViewModel.
