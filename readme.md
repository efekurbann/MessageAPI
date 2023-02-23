# MessageAPI

**Reminder**: This API is designed for jedis (redis) and you need a Redis server in order to use this API.

## Using MessageAPI

Don't forget to replace `VERSION` and `PLATFORM`!
<br>
For example:
- `VERSION` to `2.0.1` (currently the latest version)
- `PLATFORM` to `messageapi-bukkit` (or any other module name)

Gradle:

```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.CriusDevelopment.MessageAPI:MessageAPI:VERSION'
    
    // you don't need platform dependency if you will use the plugin or create your own platform

    implementation 'com.github.CriusDevelopment.MessageAPI:PLATFORM:VERSION'

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
    <groupId>com.github.CriusDevelopment.MessageAPI</groupId>
    <artifactId>MessageAPI</artifactId>
    <version>VERSION</version>
    <!-- you can use provided if you will use the plugin -->
    <!-- <scope>provided</scope> -->
</dependency>

<!-- you don't need platform dependency if you will use the plugin or create your own platform -->
<dependency>
    <groupId>com.github.CriusDevelopment.MessageAPI</groupId>
    <artifactId>PLATFORM</artifactId>
    <version>VERSION</version>
</dependency>
```

## Okay, I set up everything, how can I use the API?

### If you have plugin installed, or you will require to use the plugin. 
**(!)** Your plugin must (soft)depend on MessageAPI in order to use.
```java
class SomeClass {
    
    private MessageAPI messageAPI;

    public void someMethod() {
        this.messageAPI = MessageAPI.getInstance();

        Channel<String> channel = new Channel<>(this.messageAPI, "some-channel", String.class);
        channel.listen(message -> this.messageAPI.getLogger().info(message));
        channel.sendMessage("test message");
    }
    
}
```

### If your platform is supported, but you do not want to depend on any plugin
Don't forget to replace `YourPlatformScheduler` and `YourPlatformLogger`. This may require you passing some parameters.
<br><br>
For example: <br>
    - YourPlatformScheduler -> BukkitScheduler
<br>- YourPlatformLogger -> BukkitLogger
```java
class SomeClass {
    
    private MessageAPI messageAPI;

    public void someMethod() {
        JedisCredentials credentials = new JedisCredentials("host", "password", 1234);

        this.messageAPI = new MessageAPI(true, credentials, new YourPlatformScheduler(), new YourPlatformLogger());

        Channel<String> channel = new Channel<>(this.messageAPI, "some-channel", String.class);
        channel.listen(message -> this.messageAPI.getLogger().info(message));
        channel.sendMessage("test message");
    }
    
}
```

### If your platform is not supported
Even if your platform is not supported yet, you can implement some methods and interfaces and use MessageAPI.
```java
class SomeClass {
    
    private MessageAPI messageAPI;

    public void someMethod() {
        JedisCredentials credentials = new JedisCredentials("host", "password", 1234);

        this.messageAPI = new MessageAPI(true, credentials, new MyScheduler(), new MyLogger(this));

        Channel<String> channel = new Channel<>(this.messageAPI, "some-channel", String.class);
        channel.listen(message -> this.messageAPI.getLogger().info(message));
        channel.sendMessage("test message");
    }
    
    class MyLogger implements Logger {
        
        @Override
        public void log(String message) {
            // TODO fill
        }
        
    }

    class MyScheduler implements Scheduler {

        @Override
        public void runAsync(Runnable runnable) {
            // TODO fill
        }

        @Override
        public void runSync(Runnable runnable) {
            // TODO fill
        }

        @Override
        public void runSyncTimer(Runnable runnable, long delay, long period) {
            // TODO fill
        }

        @Override
        public void runAsyncTimer(Runnable runnable, long delay, long period) {
            // TODO fill
        }

    }
    
}
```
