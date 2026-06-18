# Release 26.2-2.7.0
- Update to Minecraft 26.2 

### Breaking changes for datapacks!
- custom category display configuration
  - removed array field `styles`
  - add string field `color` - accepts either hex code such as `#FF0000` or a color name such as `red`
  - change was needed due to how Minecraft now handles text
  - older datapacks should work, but categories will not have the custom display style