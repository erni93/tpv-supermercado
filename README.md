# TPV – Supermercado
![tpv-supermercado-1](https://cloud.githubusercontent.com/assets/16189689/20643002/8b12d1f4-b41d-11e6-9182-6ab67a3c800b.png)
# 1. Descripción y análisis del proyecto

El objetivo del proyecto es realizar una aplicación que gestione todo lo relacionado a la gestión de un supermercado. Artículos, empleados, categorías, descuentos. Además, deberá disponer de un terminal de venta.

El software deberá acceder al sistema de inventario para consultar precio y actualizar la cantidad.

El software deberá generar el ticket y conectar con el sistema de ventas para registrar la venta.

# 2. Base de datos

Los datos se almacenan en una base de datos MYSQL, a modo de demostración se dispone de una configuración por defecto donde conecta con una base de datos alojada en internet, permitiendo acceder a las distintas opciones desde cualquier parte sin requerir un servidor local.

La dirección del panel de administración de la base de datos online es http://13.81.14.51/phpmyadmin/

Los datos para gestionar la base de datos de muestra “Proyecto” son los siguientes:

Usuario: proyecto

Contraseña: galileo

El proyecto también incluye un fichero de configuración para importar el diseño de base de datos a un sistema local.

## 2.1 Diseño base de datos

La base de datos cuenta con un total de 6 tablas.
* articulo
* categoria
* descuento
* empleado
* factura
* linea_factura

-> Tabla artículo: Sus columnas son:
* cod_articulo: Clave primaria del artículo.
* nombre: Nombre identificativo del artículo, para mostrarlo en la factura y en el terminal de ventas.
* stock: Cantidad disponible en el almacén.
* porcentaje_iva: Iva aplicado.
* imagen: Imagen del artículo.
* categoría: Clave foránea a la clave primaria de categoría. Indica a que categoría pertenece el artículo.

![tpv-supermercado-2](https://cloud.githubusercontent.com/assets/16189689/20643003/8b149f5c-b41d-11e6-8595-2a5b2c06d771.png)

-> Tabla categoría: Sus columnas son:
* cod_categoria: Clave primaria de la categoría.
* nombre: Nombre identificativo de la categoría, para mostrarlo en los combo box.
* imagen: Imagen de la categoría.
* responsable: Clave foránea a la tabla empleado, indica el responsable de dicha categoría.

![tpv-supermercado-3](https://cloud.githubusercontent.com/assets/16189689/20643004/8b1f68f6-b41d-11e6-8304-9e781d36f71f.png)

-> Tabla descuento: Sus columnas son:
* cod_descuento: Clave primaria de descuento.
* articulo: Clave foránea a la tabla artículo, indica a que artículo pertenece el descuento.
* porcentaje_descuento: cantidad de descuento.
* fecha_validez: Indica la fecha máxima de validez del descuento.

![tpv-supermercado-4](https://cloud.githubusercontent.com/assets/16189689/20643005/8b1fa35c-b41d-11e6-8495-bbb424572af2.png)

-> Tabla empleado: Sus columnas son:
* cod_empleado: Clave primara del empelado.
* dni: Dni del empleado.
* nombre: Nombre del empleado.
* apellidos: Apellidos del empleado.
* genero: H si es masculino, M si es femenino, y A si son ambos.
* fecha_nacimiento: Fecha de nacimiento del empleado.
* fecha_contratacion: Fecha de contratación del empleado.
* puesto: Indica el puesto del empleado, Director, encargado, empleado.
* sueldo_bruto: Sueldo bruto del empleado, sin bonificaciones.
* sueldo_bonificacion: Bonificaciones añadidas al sueldo bruto del empleado.
* sueldo_penalizacion: Penalización añadida al sueldo del empleado.

![tpv-supermercado-5](https://cloud.githubusercontent.com/assets/16189689/20643006/8b274b2a-b41d-11e6-8841-77fe019636b1.png)

-> Tabla factura: Sus columnas son:
* cod_factura: Clave primaria de factura.
* empleado: Empleado que ha generado la factura.
* fecha: Fecha de creación de la factura.

![tpv-supermercado-6](https://cloud.githubusercontent.com/assets/16189689/20643007/8b275c3c-b41d-11e6-8469-7833e49ea713.png)

-> Tabla línea_factura Sus columnas son
* cod_linea_factura: Clave primaria de línea_factura.
* venta: Indica a que factura pertenece.
* articulo: Indica el artículo que contiene la línea.
* Cantidad: Cantidad del artículo añadido.

![tpv-supermercado-7](https://cloud.githubusercontent.com/assets/16189689/20643008/8b285646-b41d-11e6-8505-f5ad8846a060.png)

## 2.2 Diagrama E/R base de datos

![tpv-supermercado-8](https://cloud.githubusercontent.com/assets/16189689/20643009/8b2a1cf6-b41d-11e6-83d1-88066ce3c9b4.png)

## 2.3 Importar base de datos

Al importar el fichero de configuración de la base de datos, hay que realizar una configuración previa debido al peso del fichero sql.

Nos dirigimos al fichero de configuración php de nuestro xampp o appserver.

Por defecto la ruta es C:\xampp\php

Editamos el fichero php.ini Buscamos upload_max_filesize línea 798.Y post_max_size línea 656.

Cambiamos su valor a 40M por ejemplo, esto permite subir al gestor de base de datos phpmyadmin ficheros con un gran tamaño.

# 3. Desarrollo del proyecto

El proyecto cuenta con las siguientes clases:

* Inicio: Incluye un asistente de configuración de conexión.

* Principal: Menú principal que permite administrar la base de datos o iniciar el terminal de ventas.

* Conexión: Clase utilizada para conectarse a la base de datos, contiene además información principal de la empresa.

* GuardarConexion: Permite guardar la conexión en un fichero, guarda el usuario y contraseña para que al leer el fichero vuelva a construir el objeto Connection, esto es así porque la clase Connection no es serializable.

* AdministrarArticulos: Gestiona la tabla de artículos de la base de datos.

* Articulo: Contiene los mismos campos que la tabla artículo de la base de datos. Además, contiene métodos estáticos para obtener artículos filtrando por categorías.

* AdministrarCategorias: Gestiona la tabla de categorías de la base de datos.

* Categoria: Contiene los mismos campos que la tabla categoría de la base de datos. Además, contiene métodos estáticos para obtener las categorías filtrando por encargados.

* AdministrarEmpleados: Gestiona la tabla de empleados de la base de datos.

* Empleado: Contiene los mismos campos que la tabla empleado de la base de datos. Además, contiene métodos estáticos para obtener los empleados

* AdministrarDescuentos: Gestiona la tabla de descuentos de la base de datos.

* Descuento: Contiene los mismos campos que la tabla descuento de la base de datos. Además, contiene métodos estáticos para obtener los descuentos.

* Factura: Contiene los mismos campos que la tabla factura de la base de datos. Contiene métodos estáticos para obtener el número de factura siguiente.

* LineaFactura: Contiene los mismos campos que la tabla línea_factura de la base de datos. Contiene métodos estáticos para obtener el número de línea siguiente.

* RegistroVentas: Gestiona la tabla factura de la base de datos, permite generar archivos pdf de las facturas ya creadas.

* Calculadora: Aplicación para realizar cálculos con el ratón.

* Operación: Utilizado en calculadora para registrar las operaciones realizadas, y guardar un historial de estas.

* TerminalVentas: Permite realizar una nueva factura, queda registrado el empleado que realiza dicha venta.

La mayoría de clases incluyen la interface Comparable para poder ordenar los artículos, categorías, empleados etc.

Debido al elevado número de imágenes, el programa se ejecuta más rápido en un entorno local.

# 4. Manual de uso

Al iniciar el programa por primera vez, se iniciará el asistente de configuración.

![tpv-supermercado-9](https://cloud.githubusercontent.com/assets/16189689/20642989/8ae8bcac-b41d-11e6-93c2-6d2e46578ac2.png)

![tpv-supermercado-10](https://cloud.githubusercontent.com/assets/16189689/20642991/8ae9d1b4-b41d-11e6-9b70-60b87b19e309.png)

El asistente intentar leer de nuevo el fichero configuración.dat, o en su defecto crear una nueva conexión.

Seleccionamos crear una nueva conexión, nos aparecerá la siguiente ventana.

![tpv-supermercado-11](https://cloud.githubusercontent.com/assets/16189689/20642990/8ae95ef0-b41d-11e6-8b52-bfe989b407f9.png)

Tenemos dos posibilidades
* Utilizar la configuración por defecto online, esto permite gestionar nuestro programa sin depender de un servidor local. Para elegir esta opción pulsamos en “Cargar base de datos con productos de prueba”.

![tpv-supermercado-12](https://cloud.githubusercontent.com/assets/16189689/20642992/8af0a3a4-b41d-11e6-9049-0d46a9040f61.png)

* Si preferimos utilizar nuestra base de datos, rellenamos los campos correspondientes.

![tpv-supermercado-13](https://cloud.githubusercontent.com/assets/16189689/20642988/8ae8305c-b41d-11e6-88d2-de9b4b29f0ba.png)

Al darle a continuar verificará que la configuración sea correcta.

![tpv-supermercado-14](https://cloud.githubusercontent.com/assets/16189689/20642987/8ae3d034-b41d-11e6-8137-ca578b23e5c0.png)

Una vez tengamos una conexión configurada, cada vez que abramos nuestro programa nos encontraremos con el panel de autentificación.

![tpv-supermercado-15](https://cloud.githubusercontent.com/assets/16189689/20642993/8af78d90-b41d-11e6-964d-0bc811b7c4a3.png)

Deberemos elegir el empleado que inicia el programa.

Las contraseñas por defecto son:

* Grupo director: director2016
* Grupo encargado: encargado2016
* Grupo empleado: empleado

Los empleados únicamente pueden iniciar el terminal de ventas, para gestionar la base de datos hay que autentificarse con un encargado o el director.

En la parte inferior izquierda se muestra el tipo de permiso del empleado seleccionado.

Si entramos con un encargado o empleado podemos utilizar todas las opciones.

![tpv-supermercado-16](https://cloud.githubusercontent.com/assets/16189689/20642994/8afbe408-b41d-11e6-9bee-11de6368f292.png)

Al entrar con un empleado el acceso estará restringido.

![tpv-supermercado-17](https://cloud.githubusercontent.com/assets/16189689/20642995/8afd3286-b41d-11e6-8fb8-cb16412c8f14.png)

El empleado únicamente podrá realizar ventas.

Desde el administrador de empleados podemos consultar la lista de empleados, también es posible editar, borrar sus datos o crear un nuevo empleado.

![tpv-supermercado-18](https://cloud.githubusercontent.com/assets/16189689/20642996/8afe7a42-b41d-11e6-8ed7-ca288d5b0541.png)

El administrador de descuentos permite consultar los descuentos disponibles, editar, borrar o crear nuevos descuentos.

Un descuento tiene una fecha de validez máxima, si un artículo contiene más de un descuento con fecha validas, el sistema elije el descuento mas alto.

![tpv-supermercado-19](https://cloud.githubusercontent.com/assets/16189689/20642997/8afee09a-b41d-11e6-9b33-b08e58917508.png)

En el administrador de categorías podemos buscar, editar, eliminar, crear nuevas categorías, a la vez que asignar un responsable para dicha categoría.

![tpv-supermercado-20](https://cloud.githubusercontent.com/assets/16189689/20642998/8b0a561e-b41d-11e6-819b-0dd8556d6a63.png)

El administrador de artículos también permite buscar, editar, eliminar, crear artículos, debido al alto número de artículos, es posible filtrar por categoría a la hora de buscar artículos.

![tpv-supermercado-21](https://cloud.githubusercontent.com/assets/16189689/20642999/8b0b94b6-b41d-11e6-8095-1516f3fb1014.png)

En el registro de ventas podemos observar las distintas facturas creadas, también es posible eliminar facturas. Es posible crear un PDF de la factura seleccionada.

![tpv-supermercado-22](https://cloud.githubusercontent.com/assets/16189689/20643000/8b11c43a-b41d-11e6-8d33-3ec7222944fb.png)

El terminal de ventas es la parte más importante del programa, permite realizar nuevas facturas, añadiendo a una tabla los distintos artículos que el cliente ha elegido, al final permite generar un ticket/pdf para imprimirlo posteriormente.

![tpv-supermercado-23](https://cloud.githubusercontent.com/assets/16189689/20643001/8b12899c-b41d-11e6-8fc1-1963e7d09c50.png)


