# Tools

Developer and maintenance utilities live under `tools/` instead of cluttering the repository root.

## Windows helpers

`tools/windows/` contains Windows-friendly helpers for common local tasks:

- `BUILD-RELEASE.bat`
- `BUILD-DEBUG.bat`
- `UPDATE-REPO.bat`
- `UPDATE-DEPENDENCIES.bat`
- `UPDATE-SOURCE.bat`

See [Windows tools](WINDOWS-TOOLS.md) for usage.

## Maintenance scripts

`tools/scripts/` contains maintenance utilities:

- `compose_svg_drawable.py`
- `download_material_icons.py`
- `send_telegram.py`
- `update_dependencies.ps1`
- `update_source.ps1`

The scripts are grouped by purpose while keeping paths used by the build and GitHub Actions stable.
