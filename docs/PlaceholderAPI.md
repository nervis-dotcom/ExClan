# ExClan – Placeholders (PlaceholderAPI)

Este documento describe todas las variables disponibles mediante **PlaceholderAPI** para el plugin **ExClan**.

**Identificador base:**
```
%exclan_<categoría>_<args>%
```

Las categorías principales son:

1. `player` – Información del clan del jugador.
2. `clan` – Información de un clan específico.
3. `top` – Rankings de clanes.

---

# 1. Placeholders: PLAYER
Prefijo:
```
%exclan_player_<variable>%
```

Requiere un jugador online.  
Si el jugador no tiene clan: devuelve **"sin clan"**.

## Información visual
| Placeholder                   | Descripción                 |
|-------------------------------|-----------------------------|
| `%exclan_player_symbol-icon%` | Icono del símbolo asignado  |
| `%exclan_player_symbol-name%` | Nombre del símbolo asignado |

## Datos del clan del jugador
| Placeholder                   | Descripción       |
|-------------------------------|-------------------|
| `%exclan_player_clan%`        | Nombre del clan   |
| `%exclan_player_tag%`         | Tag del clan      |
| `%exclan_player_discord%`     | Discord del clan  |
| `%exclan_player_rank%`        | Rango del jugador |
| `%exclan_player_description%` | Descripción       |

## Líder
| Placeholder                   | Descripción      |
|-------------------------------|------------------|
| `%exclan_player_leader%`      | Nombre del líder |
| `%exclan_player_leader-uuid%` | UUID del líder   |

## Estadísticas del clan
| Placeholder              | Descripción |
|--------------------------|-------------|
| `%exclan_player_points%` | Puntos      |
| `%exclan_player_kills%`  | Kills       |
| `%exclan_player_bank%`   | Banco       |

## Posiciones en rankings
| Placeholder                  | Descripción        |
|------------------------------|--------------------|
| `%exclan_player_top-kills%`  | Ranking por kills  |
| `%exclan_player_top-points%` | Ranking por puntos |
| `%exclan_player_top-bank%`   | Ranking por banco  |

## Configuraciones del clan
| Placeholder                | Descripción       |
|----------------------------|-------------------|
| `%exclan_player_pvp%`      | PvP activado      |
| `%exclan_player_pvp-ally%` | PvP entre aliados |

## Miembros, aliados y baneados
| Placeholder                      | Descripción       |
|----------------------------------|-------------------|
| `%exclan_player_members-amount%` | Total miembros    |
| `%exclan_player_allys-amount%`   | Total aliados     |
| `%exclan_player_banned-amount%`  | Total baneados    |
| `%exclan_player_members%`        | Lista de miembros |
| `%exclan_player_allys%`          | Lista de aliados  |
| `%exclan_player_banned%`         | Lista de baneados |
---

# 2. Placeholders: CLAN
Prefijo:
```
%exclan_clan_<nombreClan>_<variable>%
```

Ejemplo:
```
%exclan_clan_excalibur_points%
```

## Información general
| Placeholder                        | Descripción     |
|------------------------------------|-----------------|
| `%exclan_clan_<name>_clan%`        | Nombre del clan |
| `%exclan_clan_<name>_tag%`         | Tag del clan    |
| `%exclan_clan_<name>_discord%`     | Discord         |
| `%exclan_clan_<name>_description%` | Descripción     |

## Líder
| Placeholder                        | Descripción      |
|------------------------------------|------------------|
| `%exclan_clan_<name>_leader%`      | Nombre del líder |
| `%exclan_clan_<name>_leader-uuid%` | UUID del líder   |

## Estadísticas
| Placeholder                   | Descripción |
|-------------------------------|-------------|
| `%exclan_clan_<name>_points%` | Puntos      |
| `%exclan_clan_<name>_kills%`  | Kills       |
| `%exclan_clan_<name>_bank%`   | Banco       |

## Miembros, aliados y baneados
| Placeholder                           | Descripción       |
|---------------------------------------|-------------------|
| `%exclan_clan_<name>_members-amount%` | Total miembros    |
| `%exclan_clan_<name>_allys-amount%`   | Total aliados     |
| `%exclan_clan_<name>_banned-amount%`  | Total baneados    |
| `%exclan_clan_<name>_members%`        | Lista de miembros |
| `%exclan_clan_<name>_allys%`          | Lista de aliados  |
| `%exclan_clan_<name>_banned%`         | Lista de baneados |

## Configuraciones del clan
| Placeholder                     | Descripción       |
|---------------------------------|-------------------|
| `%exclan_clan_<name>_pvp%`      | PvP activado      |
| `%exclan_clan_<name>_pvp-ally%` | PvP entre aliados |

## Símbolos por rango
```
%exclan_clan_<name>_symbols-icon_<rank>%
%exclan_clan_<name>_symbols-name_<rank>%
```

## Rankings
| Placeholder                       | Descripción        |
|-----------------------------------|--------------------|
| `%exclan_clan_<name>_top-kills%`  | Posición en kills  |
| `%exclan_clan_<name>_top-points%` | Posición en puntos |
| `%exclan_clan_<name>_top-bank%`   | Posición en banco  |

---

# 3. Placeholders: TOP  
Prefijo:
```
%exclan_top_<tipo>_<argumento>%
```
## Tipos

- kills  
- points 
- bank   

## Argumento

- nombre del clan


## A) Obtener nombre del clan según posición
```
%exclan_top_points_1%
```

## B) Obtener posición del clan por nombre
```
%exclan_top_points_excalibur%
```