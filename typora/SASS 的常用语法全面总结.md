### SASS 的常用语法全面总结

SASS 是 CSS 的扩展，提供了更高效和模块化的语法结构，方便开发者编写更清晰和可维护的代码。以下是 SASS 的核心功能和常用语法：

------

### 1. **变量（Variables）**

变量用于存储常量值（如颜色、字体大小等），以便重复使用。

```scss
$primary-color: #3498db;
$font-size: 16px;

body {
  color: $primary-color;
  font-size: $font-size;
}
```

------

### 2. **嵌套（Nesting）**

SASS 支持嵌套选择器，便于描述层级关系。

```scss
.navbar {
  background: #f8f9fa;

  ul {
    list-style: none;

    li {
      display: inline-block;

      a {
        text-decoration: none;
        color: #000;
      }
    }
  }
}
```

------

### 3. **Partials 和 @import**

SASS 支持将样式拆分成多个文件，使用 `@import` 将其组合在一起。这种机制被称为 **Partials**。

- **Partials 文件**：以 `_` 开头的文件不会被单独编译。例如，创建一个 `_variables.scss` 文件：

  ```scss
  // _variables.scss
  $primary-color: #3498db;
  $secondary-color: #2ecc71;
  ```

- **在主文件中导入**：

  ```scss
  // styles.scss
  @import 'variables';
  
  body {
    background-color: $primary-color;
    color: $secondary-color;
  }
  ```

> **注意**：在 SASS 的最新版本中，推荐使用 `@use` 和 `@forward` 替代 `@import`，以避免全局变量污染。

------

### 4. **Mixin（混合）**

Mixin 是可复用的代码块，可以接收参数，用于减少重复代码。

```scss
@mixin flex-center($direction: row) {
  display: flex;
  justify-content: center;
  align-items: center;
  flex-direction: $direction;
}

.container {
  @include flex-center(column); // 使用时传递参数
  height: 100vh;
}
```

------

### 5. **继承（Inheritance）**

SASS 支持继承另一个选择器的样式，使用 `@extend` 实现。

```scss
.button {
  padding: 10px 20px;
  border: none;
  background-color: $primary-color;
}

.primary-button {
  @extend .button;
  color: white;
}
```

------

### 6. **操作（Operators）**

SASS 支持数学和颜色操作。

```scss
$base-padding: 10px;

.container {
  padding: $base-padding * 2; // 结果：20px
}

.header {
  color: lighten($primary-color, 20%); // 调亮颜色
}
```

------

### 7. **函数（Functions）**

SASS 提供了许多内置函数（如 `lighten`、`darken` 等），也支持自定义函数。

```scss
// 内置函数
$dark-blue: darken(#3498db, 10%);
$light-blue: lighten(#3498db, 20%);

.card {
  background-color: $dark-blue;
}

// 自定义函数
@function calculate-rem($px-value) {
  @return $px-value / 16 * 1rem;
}

.content {
  font-size: calculate-rem(24); // 1.5rem
}
```

------

### 8. **条件和循环**

#### 条件语句（@if/@else）

```scss
$theme: dark;

body {
  @if $theme == light {
    background: #fff;
    color: #000;
  } @else {
    background: #000;
    color: #fff;
  }
}
```

#### 循环（@for, @each, @while）

```scss
// @for 循环
@for $i from 1 through 5 {
  .col-#{$i} {
    width: 20% * $i;
  }
}

// @each 循环
$colors: red, green, blue;

@each $color in $colors {
  .#{$color}-text {
    color: $color;
  }
}
```

------

### 9. **@use 和 @forward（替代 @import 的现代方法）**

SASS 提供了新的模块系统：`@use` 和 `@forward`，更推荐使用它们。

#### @use

- 默认不会将变量和 Mixin 引入全局作用域，必须通过模块名引用。

```scss
// _variables.scss
$primary-color: #3498db;

@mixin border-radius($radius) {
  border-radius: $radius;
}

// styles.scss
@use 'variables';

.button {
  color: variables.$primary-color; // 引用变量
  @include variables.border-radius(10px); // 引用 Mixin
}
```

#### @forward

- 用于重新导出模块的内容。

```scss
// _theme.scss
$primary-color: #3498db;
$secondary-color: #2ecc71;

// _index.scss
@forward 'theme';

// styles.scss
@use 'index' as theme;

body {
  color: theme.$primary-color;
}
```

------

### 10. **插值（Interpolation）**

插值用于动态生成样式名或属性值。

```scss
$size: large;

.icon-#{$size} {
  width: 50px;
  height: 50px;
}
```

------

### 11. **占位符选择器（%）**

占位符选择器（`%`）可以作为一个样式模板，与 `@extend` 配合使用。

```scss
%button {
  padding: 10px 20px;
  border: none;
  font-size: 16px;
}

.primary-button {
  @extend %button;
  background-color: $primary-color;
}

.secondary-button {
  @extend %button;
  background-color: $secondary-color;
}
```

------

### 12. **注释**

- 单行注释：不会编译到 CSS 中。

  ```scss
  // 这是单行注释
  ```

- 多行注释：会编译到 CSS 中。

  ```scss
  /* 这是多行注释 */
  ```

------

### 13. **使用父选择器 `&`**

`&` 表示父级选择器，可以用在伪类或嵌套规则中。

```scss
.button {
  color: $primary-color;

  &:hover {
    color: white;
  }

  &-primary {
    background-color: $primary-color;
  }
}
```

------

### @import 使用说明

#### 基本用法

`@import` 用于引入其他 SASS 文件：

```scss
@import 'reset'; // 引入 reset.scss 文件
```

#### 使用时的注意事项

1. 不需要写文件扩展名，SASS 会自动识别 `.scss` 或 `.sass` 文件。
2. 如果文件以 `_` 开头（如 `_variables.scss`），表示这是一个部分文件（Partial），不会单独编译为 CSS 文件。

#### 局限性

1. `@import` 会将所有变量和样式导入到全局作用域，可能造成变量名冲突。
2. 会引入多次相同文件的问题。

因此，推荐使用 `@use` 和 `@forward` 替代 `@import`。

------

### 插值（Interpolation）

#### 用法

插值语法 `#{$expression}` 用于在 SASS 中动态生成 CSS 的类名、属性名、或属性值。它可以插入变量、计算结果等。

#### 示例

```scss
$size: large;

.icon-#{$size} {
  width: 50px;
  height: 50px;
}

$property: background-color;
.container {
  #{$property}: red; // 动态生成属性
}
```

#### 输出的 CSS

```css
.icon-large {
  width: 50px;
  height: 50px;
}

.container {
  background-color: red;
}
```

------

### 占位符选择器（%）

#### 用法

占位符选择器 `%name` 是一种抽象选择器，不能直接编译为 CSS，但可以通过 `@extend` 将样式引入其他选择器中。它的好处是提高代码复用性，同时避免直接输出无用的 CSS。

#### 示例

```scss
%button {
  padding: 10px 20px;
  border: none;
  font-size: 16px;
}

.primary-button {
  @extend %button;
  background-color: blue;
  color: white;
}

.secondary-button {
  @extend %button;
  background-color: green;
  color: black;
}
```

#### 输出的 CSS

```css
.primary-button, .secondary-button {
  padding: 10px 20px;
  border: none;
  font-size: 16px;
}

.primary-button {
  background-color: blue;
  color: white;
}

.secondary-button {
  background-color: green;
  color: black;
}
```

------

### 使用父选择器 `&`

#### 用法

父选择器 `&` 表示当前选择器，可以用于嵌套时引用父级选择器的名称。常用于伪类、伪元素、或者类名扩展。

#### 示例

```scss
.button {
  color: blue;

  &:hover {
    color: white; // 父级的 hover 样式
  }

  &-primary {
    background-color: blue; // 扩展类名为 button-primary
  }

  &.active {
    border: 1px solid red; // 当前类和 active 一起使用时的样式
  }
}
```

#### 输出的 CSS

```css
.button {
  color: blue;
}

.button:hover {
  color: white;
}

.button-primary {
  background-color: blue;
}

.button.active {
  border: 1px solid red;
}
```

------

### 综合示例

以下代码结合了插值、占位符选择器和父选择器：

```scss
$base-name: card;

%shared-styles {
  border: 1px solid #ddd;
  padding: 10px;
}

.#{$base-name} {
  @extend %shared-styles;
  background-color: white;

  &-header {
    font-size: 20px;
    font-weight: bold;
  }

  &-content {
    font-size: 16px;
    color: gray;
  }

  &:hover {
    background-color: #f5f5f5;
  }
}
```

#### 输出的 CSS

```css
.card {
  border: 1px solid #ddd;
  padding: 10px;
  background-color: white;
}

.card-header {
  font-size: 20px;
  font-weight: bold;
}

.card-content {
  font-size: 16px;
  color: gray;
}

.card:hover {
  background-color: #f5f5f5;
}
```

------

### 总结

- **插值**：用于动态生成类名、属性名、或属性值，例如生成 `.icon-large`、`background-color: red`。
- **占位符选择器**：通过 `%` 创建抽象选择器，配合 `@extend` 使用，不直接输出 CSS，提高复用性。
- **父选择器 `&`**：表示当前选择器，用于伪类、扩展类名或组合选择器时，生成如 `.button:hover` 或 `.button-primary` 等规则。

通过这三种语法，可以大幅提高样式开发的灵活性和可维护性，同时生成清晰的 CSS 输出。

SASS 提供了强大的功能，包括变量、嵌套、Mixin、继承、条件、循环等，使得样式编写更加高效和灵活。在现代 SASS 中，`@use` 和 `@forward` 是模块化开发的主流推荐。通过合理地使用这些功能，可以显著提高 CSS 开发的效率和代码的可维护性。