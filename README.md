# JUMP2DIGITAL / Skins API
Por: Javier Albors Sánchez 

## Modelo de datos 
Con el fin de que esta aplicación fuese lo más escalable posible a nivel de usuarios, se han diseñado las siguientes tablas **MySQL**:

- `Skin`: Esta tabla define la información de cada _skin_ y contiene los siguientes atributos:
  * `id`: Identificador único de la _skin_
  * `name`: Nombre de la _skin_
  * `price`: Precio de la _skin_
  * `default_color`: Identificador que hace referencia al color por defecto de la _skin_

- `Color`: Esta tabla define la información de los diferentes colores que puedan ser almacenados en la base de datos. Sus atributos son:
  * `id`: Identificador único del color
  * `hex`: Código hexadecimal del color en cuestión

- `Type`: Esta tabla define la información de los diferentes tipos que puede tener una _skin_. Sus atributos son los siguientes:
  * `id`: Identificador único del tipo de _skin_
  * `name`: Nombre del tipo de _skin_

- `User`: Esta tabla almacena los usuarios que se registren en el sistema. Cada usuario tendrá los siguientes atributos:
  * `id`: Identificador único del usuario
  * `username`: Nombre de usuario
  * `password`: Contraseña del usuario en cuestión. Esta contraseña se somete al algoritmo de _hashing_ 'Argon2' antes de ser guardada en la base de datos
  * `api_key`: Clave para identificar al usuario cuando éste quiera realizar peticiones a la API para obtener información de su propia cuenta
  * `funds`: Cantidad de créditos que tiene una cuenta de usuario. Por defecto, cada usuario recién registrado recibirá 100 créditos.

El diseño de este modelo de datos, permite que cada _skin_ pueda ser de más de un tipo, y también permite que cada usuario pueda tener más de una _skin_ (siempre y cuando sean distintas). También se permite al usuario cambiar el color de una _skin_ que tenga en su posesión. 
Es por estas razones por las que también se han creado las siguientes tablas relacionales: 

- `SkinType`: Esta tabla define los tipos que puede tener cada _skin_ y tiene los siguientes atributos:
  * `skin`: Identificador que hace referencia a la skin en cuestión
  * `type`: Identificador que hace referencia al tipo de _skin_ del que forma parte la _skin_ `skin`

- `UserSkinColor`: Esta tabla define las _skins_ que tiene en posesión cada usuario, además del color de cada una de estas. Los atributos de esta tabla son:
  * `user`: Identificador que hace referencia al usuario en cuestión
  * `skin`: Identificador que hace referencia a una _skin_ que posee el usuario en cuestión
  * `color`: Identificador que hace referencia al color de la _skin_ en cuestión del usuario en cuestión. Si el usuario nunca ha cambiado el color de una _skin_, su color será igual al color por defecto de dicha _skin_

## Autenticación 
Debido a que ciertas consultas a la API hacen referencia a un usuario propio, y el diseño de la aplicación permite ser escalable en cuanto a usuarios, se ha tenido la necesidad de crear un sistema de autenticación de usuario en ciertas llamadas a la API para obtener únicamente la información que haga referencia al usuario autenticado. 

Para ello, cuando un usuario se registre en la aplicación, se generará una clave única de 16 bytes asociada a su usuario que éste podrá obtener para añadirla como parámetro en las peticiones que necesiten de esta autenticación. 

## Endpoints 
> Se ha determinado que el _base path_ de esta API sea `/j2d/api`, por lo tanto, para poder hacer una petición al endpoint `/register` por ejemplo, deberá hacerse sobre `j2d/api/register` (e.g. `http://localhost:8080/j2d/api/register`)
<br>

Esta API soporta los siguientes _endpoints_: 

- **POST** `/register`: Registra a un usuario
  Parámetros:
  * `username`
  * `password`
  
  Retorna el nombre de usuario y su API Key

- **POST** `/login`: Valida los credenciales de un usuario ya registrado anteriormente
  Parámetros:
  * `username`
  * `password`
  
  Retorna el nombre de usuario y su API Key

- **GET** `/skins/available`: Retorna una lista de las _skins_ disponibles en la base de datos junto con los tipos a los que pertenece y el color por defecto de cada _skin_

- **GET** `/skins/getskin/{id}`: Retorna la información completa de la _skin_ especificada por el valor de `{id}` de la URI

- **POST** `/skins/buy`: Compra una _skin_ para el usuario autenticado
  Parámetros:
  * `skinID`
  * `apiKey`

  Retorna el nombre del usuario autenticado junto con los créditos que tiene disponibles después de haber realizado la compra de la _skin_

- **GET** `/skins/myskins: Retorna todas las _skins_ de las que el usuario autenticado es dueño
    Parámetros:
    * `apiKey`
   
- **PUT** `/skins/color`: Cambia el color de una _skin_ que tenga en posesión el usuario autenticado
    Parámetros:
    * `skinID`
    * `color`: Código del nuevo color en hexadecimal
    * `apiKey`

    Retorna la información de la _skin_ de la cual se ha actualizado el color

- **DELETE** `/skins/delete/{id}`: Elimina la _skin_ especificada por el valor de `{id}` siempre que esté en posesión del usuario autenticado
    Parámetros:
    * `apiKey`

    Retorna la lista de _skins_ restantes en la cuenta del usuario autenticado
<br>

> Cabe destacar que todos los parámetros de los _endpoints_ son obligatorios, y que los mensajes de retorno explicados en cada uno de estos _endpoints_ ocurrirán cuando las llamadas a la API se hayan realizado correctamente. En caso contrario, se retornará un mensaje de error informativo por cada distinto caso
<br>

## ¿Cómo utilizar?
### Proyecto Java
Esta aplicación se ha realizado utilizando el IDE `IntelliJ IDEA` y las tecnologías `Java 21`, `Spring Boot Framework` y `MySQL`.

Para poder iniciar el servidor API, es recomendable crear un proyecto `Maven` para gestionar las dependencias que se utilizan en el proyecto fácilmente mediante el fichero `pom.xml` de este repositorio.
Para incluir estas dependencias en el proyecto, se puede utilizar el comando `mvn clean install` (o click derecho en **pom.xml** > Maven > Reload project si se utiliza IntelliJ IDEA)

En el directorio **src** del repositorio, se encuentran de manera estructurada los paquetes y clases Java que utiliza la aplicación.
La clase `app.Main` (`src/main/java/app/main`) contiene el método `main` que es el que se debe configurar como clase que ejecuta de la aplicación.

### MySQL
Previamente a ejecutar la aplicación, se debe ejecutar el script SQL `j2d_videogame.sql` en un gestor de bases de datos MySQL. Este script creará la base de datos `j2d_videogame` y las tablas correspondientes para el funcionamiento de la aplicación.

Para que la aplicación Java pueda conectarse correctamente a la base de datos MySQL, en la clase `app.persistence.Database` (`src/main/java/app/persistence/database`), se pueden modificar los atributos de la clase como `HOST`, `USER`, `PASSWORD`, etc. para que la aplicación se pueda conectar correctamente con MySQL.

### Fichero skins.json
Para que la aplicación pueda iniciarse correctamente, en el directorio raíz del proyecto debe estar el fichero `skins.json`. Este fichero contiene la lista de _skins_ disponibles en la aplicación.
Este fichero se leerá al iniciarse la aplicación e insertará de manera estructurada en la base de datos las _skins_, los tipos y los colores que haya presente en él, siempre y cuando estos no existan ya en la base de datos.   
<br>
<br>
Al iniciar la aplicación, se iniciará la API en Spring Boot por defecto en el puerto 8080 de nuestro propio sistema y se podrá acceder a la API mediante peticiones HTTP a la ruta `localhost:8080/j2d/api` ó `127.0.0.1:8080/j2d/api` acompañado del _endpoint_ que se desee.

## Web de testing
Para poder probar la API de una manera más directa e interactiva, también se incluye en este proyecto el fichero `index.html` junto con la carpeta `assets`. Este fichero HTML se puede abrir de manera local en el navegador. En la página web que aparezca, se pueden enviar peticiones a cualquier _endpoint_ de la API y ver el resultado de cada petición.
