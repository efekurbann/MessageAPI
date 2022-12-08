# MessageAPI

**Reminder**: This API is designed for jedis (redis) and you need a Redis server in order to use this API.

## Using MessageAPI

Gradle:
```
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.CriusDevelopment:MessageAPI:VERSION'
    
    // you can use compileOnly if you will use the plugin
    //compileOnly 'com.github.CriusDevelopment:MessageAPI:VERSION' 
}
```

Maven:
```
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependency>
    <groupId>com.github.CriusDevelopment</groupId>
    <artifactId>MessageAPI</artifactId>
    <version>VERSION</version>
    <!-- you can use provided if you will use the plugin -->
    <!-- <scope>provided</scope> -->
</dependency>
```

### Okay, I did everything now, how can I use the API?

[You can find an example right here](https://github.com/CriusDevelopment/MessageAPI/tree/master/Plugin/src/main/java/dev/crius/messageapi/plugin)