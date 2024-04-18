
# D2D2 Components
### Visual GUI Components Extension for D2D2 Core

D2D2 Components is an extension to the [D2D2 Core framework](https://github.com/Anc3vt/d2d2-core), providing a set of visual GUI components such as buttons, lists, checkboxes, windows, text input fields, and more. These components are designed to seamlessly integrate with the [D2D2 Core framework](https://github.com/Anc3vt/d2d2-core).

## Features

- **GUI Components:** D2D2 Components offers a variety of GUI components commonly found in applications and games, including buttons, checkboxes, radio buttons, sliders, text fields, and more.

- **Customization:** Each component is highly customizable, allowing developers to adjust appearance, size, color, and behavior to suit their application's design requirements.

- **Event Handling:** Similar to [D2D2 Core](https://github.com/Anc3vt/d2d2-core), D2D2 Components supports event handling through familiar mechanisms, enabling developers to attach event listeners to GUI components and respond to user interactions effectively.

- **Integration with D2D2 Core:** D2D2 Components seamlessly integrates with [D2D2 Core](https://github.com/Anc3vt/d2d2-core), leveraging its rendering and event handling capabilities to ensure smooth performance and consistency across the entire application.

## Project Goal

The goal of D2D2 Components is to extend the functionality of D2D2 Core by providing a comprehensive library of visual GUI components, simplifying the development of Java applications and games with interactive user interfaces. By offering a flexible and easy-to-use set of components, developers can focus on building engaging user experiences without the complexity of low-level graphics programming.

## Example Usage

```java
public static void main(String[] args) {
    // Initializing the framework
    Stage stage = D2D2.init(new LwjglMediaEngine(800, 600, "Window title"));

    // Creating a frame 
    Frame panel = new Frame();
    panel.setManualResizable(true);
    panel.setDragEnabled(true);
    panel.setTitle("Window title");
    panel.setMinSize(100, 100);
    panel.setMaxSize(400, 400);

    // Creating a button
    Button button = new Button("Hello");
    button.addEventListener(Button.ButtonEvent.BUTTON_PRESSED, this::onButtonPressed);

    // Adding the button to the panel
    panel.add(button);

    // Adding the panel to the stage
    stage.add(panel);

    // Starting the rendering and event handling loop
    D2D2.loop();
}
```





## Include in your project:

To include the D2D2 Components library in your Maven project, add the following to your `pom.xml`:

In the `<repositories>` section:

```xml
<repository>
    <id>ancevt</id>
    <url>https://packages.ancevt.com/releases</url>
    <snapshots>
        <enabled>false</enabled>
        <updatePolicy>always</updatePolicy>
    </snapshots>
</repository>
<repository>
    <id>ancevt-snapshot</id>
    <url>https://packages.ancevt.com/snapshots</url>
    <snapshots>
        <enabled>true</enabled>
        <updatePolicy>always</updatePolicy>
    </snapshots>
</repository>
```

And in the `<dependencies>` section:

```xml
<dependency>
		<groupId>com.ancevt.d2d2</groupId>  
		<artifactId>d2d2-components</artifactId>  
		<version>0.0.2-beta</version>
</dependency>
```

## Contribution
Contributions to the D2D2 Components project are welcome. If you have ideas, suggestions, or bug fixes, please open a new issue or create a pull request in my GitHub repository.

