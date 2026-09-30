Design Rationale

### 1. Domain Choice
The project implements an **Incident Alerting System** designed to manage infrastructure and operations notifications[cite: 1]. The system separates the way alert messages are formatted and categorized from the communication channels used to transmit them[cite: 1].

### 2. Why Combine Bridge and Adapter?
* **Why not Bridge alone?**
  Bridge is effective at decoupling abstractions from implementations, but it assumes all implementors follow the exact same interface contract (`AlertChannel.sendAlert(header, body)`)[cite: 1]. In real-world integration scenarios, legacy hardware drivers or third-party SDKs rarely match the project's interfaces[cite: 1]. Bridge provides no mechanism to plug in an external class with conflicting method names, mismatched parameter types, and custom status codes without editing its source code or dirtying the clean interface[cite: 1].
* **Why not Adapter alone?**
  Adapter solves the compatibility barrier for a specific hardware service, but it does not prevent subclass proliferation across independent variations[cite: 1]. Without Bridge, supporting multiple notification formats (e.g., `Critical`, `Digest`) across multiple channels (Email, Telegram, Pager) causes combinatorial subclass explosion (`TelegramCriticalAlert`, `PagerDigestAlert`, etc.)[cite: 1]. Combining Bridge with Adapter enables the abstraction and implementor hierarchies to scale independently while keeping third-party legacy classes compatible[cite: 1].

### 3. Incompatibility of `LegacyPagerService`
The legacy hardware pager component (`LegacyPagerService`) cannot natively implement `AlertChannel` due to three distinct incompatibilities[cite: 1]:
1. **Method Contract:** Instead of accepting high-level message strings via `sendAlert(String, String)`, it exposes `transmitRawBuzzer(int, byte[], String)`[cite: 1].
2. **Data Types and Arguments:** It requires a numerical PIN, a raw byte array instead of Java `String` objects, and a specific hardware identifier string[cite: 1].
3. **Failure Mechanism:** The service reports errors via primitive negative integer codes (`-1` for auth failure, `-2` for buffer overflow, `-3` for unreachable hardware) rather than runtime exceptions[cite: 1].

`LegacyPagerAdapter` bridges this gap: it serializes the payload to ASCII bytes, invokes the legacy method, and translates negative status codes into standard domain-level `ChannelDeliveryException` instances[cite: 1]. This ensures that no legacy-specific error primitives escape into the higher-level abstraction layer[cite: 1].

### 4. Complexity Module: Dynamic Implementor Selection
We implemented **Dynamic Implementor Selection**[cite: 1]. Rather than hard-coding concrete channel dependencies, runtime resolution is delegated to `DynamicChannelResolver`[cite: 1]. The resolver evaluates incident parameters (such as severity scores and offline status) and dynamically chooses the proper `AlertChannel` implementation[cite: 1]. For example, low-priority alerts route to email, standard notifications to Telegram, and high-severity or offline emergencies to the hardware pager via `LegacyPagerAdapter`[cite: 1].

### 5. Known Limitation
* **Payload Truncation:** Because `LegacyPagerService` enforces a strict 64-byte payload limit on the hardware side, the adapter is forced to handle messages exceeding this capacity[cite: 1]. While modern channels like Email and Telegram easily carry arbitrary payload sizes, messages sent via the pager adapter risk losing extended contextual details to satisfy hardware constraints[cite: 1].
