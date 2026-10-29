# Rust Gutter Breakpoints for Android Studio

![Build](https://github.com/kl/gutter-breakpoints-rust-plugin/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/29173-rust-gutter-breakpoints.svg)](https://plugins.jetbrains.com/plugin/29173-rust-gutter-breakpoints)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/29173-rust-gutter-breakpoints.svg)](https://plugins.jetbrains.com/plugin/29173-rust-gutter-breakpoints)

<!-- Plugin description -->
Enables setting Rust breakpoints in Android Studio by hooking in to the existing C/C++ breakpoint support.

With this plugin you can toggle breakpoints in Rust code by clicking the gutter or by using your toggle breakpoint 
keyboard shortcut.

### RustRover integration

The plugin can integrate with RustRover (or any other IntelliJ based IDE), and have the breakpoints you set in
RustRover be synced with Android Studio automatically. That way you don't need to open the Rust file in Android Studio
at all.

To enable this simply install this plugin in Android Studio **and** in RustRover. Then setting a breakpoint in RustRover
will push the breakpoint to Android Studio by posting to a local HTTP server that is started by the plugin when it
runs in Android studio.
<!-- Plugin description end -->

### Installation

- Using the IDE built-in plugin system:
  
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Marketplace</kbd> > <kbd>Search for "Rust Gutter Breakpoints"</kbd> >
  <kbd>Install</kbd>
  
- Using JetBrains Marketplace:

  Go to [JetBrains Marketplace](https://plugins.jetbrains.com/plugin/29173-rust-gutter-breakpoints) and install it by clicking the <kbd>Install to ...</kbd> button in case your IDE is running.

  You can also download the [latest release](https://plugins.jetbrains.com/plugin/29173-rust-gutter-breakpoints/versions) from JetBrains Marketplace and install it manually using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>

- Manually:

  Download the [latest release](https://github.com/kl/gutter-breakpoints-rust-plugin/releases/latest) and install it manually using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>

### Improving LLDB interaction with common Rust types in Android Studio

First, make sure that native debugging is enabled for your debug configuration by going to `Edit configurations...` -> 
`Debugger` and selecting `Native only` or `Dual (Java + Native)` as the debug type.

**Note:** I have found that setting the debug configuration to `Dual` sometimes causes problems resolve stack variables,
so if you experience this try setting it to `Native only`.

To improve debugging Rust code in LLDB, Rust ships with the `rust-lldb` shell script (https://github.com/rust-lang/rust/blob/main/src/etc/rust-lldb),
which enables for example being able to see the text content of a `String` value in the debugger.

To enable this when debugging in Android Studio, first find out your rustc sysroot by running
`rustc --print sysroot`. Then go to `Debugger -> LLDB Startup Commands` and enter the following
two commands:

```
command script import SYSROOT_PATH/lib/rustlib/etc/lldb_lookup.py
command source SYSROOT_PATH/lib/rustlib/etc/lldb_commands
````

---
Plugin based on the [IntelliJ Platform Plugin Template][template].

[template]: https://github.com/JetBrains/intellij-platform-plugin-template
[docs:plugin-description]: https://plugins.jetbrains.com/docs/intellij/plugin-user-experience.html#plugin-description-and-presentation
