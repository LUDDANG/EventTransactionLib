<p align="center">
    <img align="center" src="images/logo.png" alt="EventTransaction"/><br/><br/>
</p>

## 개요
EventTransactionLib은 최대한 단순한 구현으로 플러그인과 모드의 이벤트 브릿지 역할을 수행하는 라이브러리입니다.

## 구현 상세
EventTransactionLib은 총 3개의 파츠로 이루어져 있습니다.

`커먼`, `플러그인`, `모드`로 구분되며, 각자 다른 역할을 수행합니다.

`커먼`은 플러그인과 모드가 서로 공유하여 사용하는 클래스입니다.

먼저 로드되는 클래스로더가 우선적으로 커먼을 통해 API를 초기화시키며, 추후 로드되는 클래스로더의 요청은 무시됩니다.

`플러그인`은 CraftBukkit API를 통해 구동되는 애드온입니다.

대부분의 모드 로더의 경우, 플러그인은 모드보다 느리게 초기화되기에 대부분의 사용에서는 ClassNotFound 오류가 발생하지 않습니다.

`모드`는 Forge 혹은 Fabric API를 통해 구동되는 애드온입니다.

플러그인보다 먼저 초기화되고, 플러그인의 클래스와 다른 클래스를 가지고 있기에 이 부분에서 특수한 처리를 통해 접근이 필요합니다.

## 사용 방법

EventTransactionLib은 하나의 모드 버킷에서 구동되는 파츠에 모두 들어가야 합니다.

Release 페이지에서 자신이 사용하는 모드 로더에 따라 다른 바이너리를 다운로드해야 합니다.

예를 들어, Arclight Forge 버킷은 EventTransactionLib-Bukkit을 plugin 폴더에,

EventTransactionLib-Forge를 mods 폴더에 동시에 설치하여 서버를 구동해야 정상적인 작동이 보장됩니다.

## 빌드 툴 적용 방법

EventTransactionLib은 하나의 커먼 모듈을 통해 모든 기능을 활용하도록 설계되었습니다.

쉐도우나 fatjar과 같은 형태가 아닌, 모드 및 플러그인의 디펜던시 형태로 작동합니다.

2가지 방법으로 적용이 가능하며, 사용하는 플랫폼에 따라 맞추어 적용해야 합니다.



### Gradle

Gradle은 compileOnly를 통한 바이너리 포함을 회피하는 형태로 적용됩니다.

다음 구문을 참고하여 반영하세요 :

```groovy
// 자바 17 이상이 필요합니다.
sourceCompatibility = targetCompatibility = "17"

// ...
repositories {
    maven {
        url "https://repo.trinarywolf.net/luddang"
        credentials {
            // 지급받은 ID를 입력하세요.
            username "ID_PROVIDED"
            // 지급받은 토큰을 입력하세요.
            password "TOKEN_PROVIDED"
        }
    }
}

dependencies {
    compileOnly("live.luya:eventtransactionlib-common:1.1.0")
    // Bukkit API를 사용하는 경우 주석을 해제하세요,
//    compileOnly("live.luya:eventtransactionlib-bukkit:1.1.0")
    // Forge API를 사용하는 경우 주석을 해제하세요,
//    compileOnly("live.luya:eventtransactionlib-forge:1.1.0")
}
```
<br/>

### Maven

Maven은 compile 스코프를 통해 패키징에 포함됨을 방지합니다.

다음 구문을 참고하여 반영하세요 :

```xml
<repositories>
    <!-- ... -->
    <repository>
        <id>trinarywolf-luddang</id>
        <name>Trinarywolf Luddang Repository</name>
        <url>https://repo.trinarywolf.net/luddang</url>
    </repository>
    <!-- ... -->
</repositories>

<dependencies>
    <!-- ... -->
    <dependency>
        <groupId>live.luya</groupId>
        <artifactId>eventtransactionlib-common</artifactId>
        <version>1.1.0</version>
    </dependency>
<!-- Bukkit API를 사용하는 경우 주석을 해제하세요 -->
<!--    <dependency>-->
<!--        <groupId>live.luya</groupId>-->
<!--        <artifactId>eventtransactionlib-bukkit</artifactId>-->
<!--        <version>1.1.0</version>-->
<!--    </dependency>-->
 <!-- Forge API를 사용하는 경우 주석을 해제하세요-->
<!--    <dependency>-->
<!--        <groupId>live.luya</groupId>-->
<!--        <artifactId>eventtransactionlib-forge</artifactId>-->
<!--        <version>1.0.1</version>-->
<!--    </dependency>-->
    <!-- ... -->
</dependencies>
```


## API
EventTransactionLib은 `EventTransactionApiProvider`을 통해 `EventTransactionApi` 인스턴스를 가져오고,

해당 인스턴스를 통해 이벤트 리스너를 등록하고, 이벤트를 호출합니다.


플러그인 혹은 모드에 디펜던시를 정확히 지정하였고, 빌드 툴 설정을 완료하였다면 API의 사용이 가능합니다.


### 주의 사항

EventTransactionLib은 클라이언트 사이드 모드가 아닙니다.

서버에만 설치하세요.

### Forge

`src/main/resources/META-INF/mods.toml`에 다음 텍스트를 상황에 알맞게 조정하여 추가합니다.

```toml
# 현재 모드의 디펜던시로 추가합니다.
[[dependencies."${mod_id}"]]
# EventTransactionLib 모드를 디펜던시로 지정합니다.
modId = "event_transaction_lib"
# 필수 디펜던시입니다.
mandatory = true
# 1.0.x의 버전만 사용 가능하도록 지정합니다.
# 문법은 마이너 버전마다 다를 수 있음으로, 와일드카드 버전은 권장되지 않습니다.
# 만약 1.0 버전 이상 모든 버전을 지정하려 한다면, "[1.0,)"으로 설정해야 합니다.
# 버전 규칙에 대한 상세한 정보는 다음 링크의 메이븐 버전 포맷을 참고하세요.
# https://cwiki.apache.org/confluence/display/MAVENOLD/Dependency+Mediation+and+Conflict+Resolution#DependencyMediationandConflictResolution-DependencyVersionRanges
versionRange = "[1.0]"
ordering = "NONE"
side = "SERVER"
```

### Bukkit
`src/main/resources/plugin.yml`에 다음 텍스트를 상황에 알맞게 조정하여 추가합니다.
```yaml
# ...
dependencies:
  - EventTransactionLib
```

### API 사용 방법
먼저, 이벤트 리스너 객체 클래스를 생성합니다.

```java
// 이벤트 리스너 등록중의 실수를 방지하기 위해 모든 이벤트 리스너는 EventTransactionListener 인터페이스를 상속해야 합니다. 
public class EventTransactionExample implements EventTransactionListener {
    @EventTransaction
    private void onExampleEvent(Example example) {
        System.out.println(example.id);
    } 
    
    public record Example(String id) {}
}
```

마지막으로, 모드의 초기화 부분에서 리스너를 등록합니다.

어떠한 시점에 초기화를 원하느냐에 따라 다른 `RegistrationOrder`을 사용해야 합니다.

```java
@Mod(ExampleMod.MODID)
public class EventTransactionExampleMod {
    public EventTransactionExampleMod() {
        EventTransactionApiProvider.getApi().prepareRegistration(
        // HYBRID_MOD_BUKKIT은 플러그인과 모드를 동시에 지원하는 버킷을 의미합니다.
        // FORGE 혹은 FABRIC의 초기화가 진행되고, PLUGIN의 초기화가 진행된 이후에 호출됩니다.         
        RegistrationOrder.HYBRID_MOD_BUKKIT, 
        api -> {
            // private 메서드라도 @EventTransaction 어노테이션이 있다면 등록됩니다.
            // 사용할 대상 플랫폼의 API를 사용하지 않으면 클래스로더 혼용 문제로 오류가 발생합니다.
            // 이 예제는 플러그인의 커먼 API를 사용했다는 가정 하에 작성되었습니다. 
            RegistrationOrder.BUKKIT.getPlatformApi().registerListener(new EventTransactionExample());	
        });
    }
}
```

플러그인, 혹은 모드에 제공될 모드는 멀티모듈 프로젝트 혹은 다른 프로젝트를 활용하여 배포 가능한 형태로 구현하는것이 좋습니다.

해당 프로젝트로 예를 들자면, 공동 작업자에게 제공할 클래스는 `common` 혹은 `api` 모듈에,

특정 플랫폼에 귀속되는 코드는 `platform`과 같은 모듈로 분리하여 제공된 모듈만 퍼블리싱하는 것이 권장됩니다.

### 유틸리티 클래스에 대한 이해
EventTransactionLib의 유틸리티는 각 플랫폼에 귀속된 유틸리티 클래스입니다.

특정 플랫폼에 귀속된 데이터 클래스를 플러그인과 모드의 통신을 위해 커먼에 포함된 클래스로 변환하거나,

그 역의 과정을 진행합니다.

어떠한 유틸리티 클래스던 간에 `EventTransactionUtil` 클래스명을 가지고 있으며, 

플랫폼 라이브러리가 추가되지 않으면 사용할 수 없습니다.



## 로고 이미지 출처

해당 프로젝트의 로고는 2개의 이미지를 합성하여 제작되었습니다.

이미지 출처는 다음의 링크를 참고하세요:

- https://www.onlinewebfonts.com/icon (다리 아이콘)
- https://www.pngarts.com/ko/explore/123827 (와이파이 아이콘)