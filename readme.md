#MergeSort
Данный проект был собран и протестирован с помощью JDK [java-11-amazon corretto-11.0.18](https://docs.aws.amazon.com/corretto/latest/corretto-11-ug/downloads-list.html).
## Инструкция по установке зависимостей
Для установки библиотеки jetbrains с помощью [Maven 4.0.0](https://maven.apache.org/docs/4.0.0-alpha-2/release-notes.html), 
необходимо в pom.xml вставить следующую зависимость:
```
<dependency>
            <groupId>org.jetbrains</groupId>
            <artifactId>annotations</artifactId>
            <version>RELEASE</version>
            <scope>compile</scope>
</dependency>
```
## Инструкция по сборке

Сборка проекта производится с помощью инструмента [Maven](https://maven.apache.org/). В терминале компьютера необходимо прописать следующие команды:

```
mvn compile 

mvn package
```