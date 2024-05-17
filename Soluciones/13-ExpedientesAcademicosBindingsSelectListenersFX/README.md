# Explicación de la aplicación

## Descripción
Ejemplo de aplicación JavaFX para gestionar una clase de alumnos. Un CRUD básico, donde nos apoyaremos en MVVM.

Gson para la importación o exportación de los datos a un fichero JSON.
Zip para la compresión y descompresión del backup.
ROP (Railway Oriented Programming) para el control de errores.

Se trabaja con estados y un ViewModel
El estado de la IU de la pantalla es aquello que necesitas mostrar en la pantalla. 
El estado de elementos que so activos o compartidos entre elementos.

El estado de la IU no es una propiedad estática, ya que los datos de la aplicación y los eventos del usuario hacen que el estado de la IU cambie con el paso del tiempo. La lógica determina los detalles del cambio, lo que incluye qué partes del estado de la IU cambiaron, por qué y cuándo debe cambiar. Esta lógica es gestionada por el ViewModel.

La clase ViewModel es una lógica empresarial o un contenedor de estado a nivel de pantalla. Expone el estado a la IU y encapsula la lógica empresarial relacionada. Su principal ventaja es que almacena en caché el estado y lo conserva durante los cambios de configuración. Esto significa que la IU no tiene que recuperar datos cuando navegas entre actividades o si sigues cambios de configuración, como cuando cambias la escena o stage.

En definitiva:
- Tenemos una vista en la que se muestran los datos de un alumno, fichero fxml.
- Tenemos un controlador por cada vista, que se encarga de gestionar los eventos de la vista y de actualizar el estado de la vista en base al binding con el estado almacenado en el ViewModel. Pare ello haremos uso de Property/ObservablesCollections y de Bindings.
- Tenemos un ViewModel que es el encargado de gestionar el estado de la vista y de la aplicación. El ViewModel es el de la lógica de negocio. Tendremos elementos de tipo Property/ObservablesCollections que serán los que se enlacen con la vista. 
- Repositorio que es el encargado de gestionar los datos de la aplicación. En este caso los datos se almacenan en un en una base de datos SQLite.
- Storage, servicio que se encarga de la importación y exportación de los datos de la aplicación, manejo de ficheros, imágenes, etc.

![mvvm](https://raw.githubusercontent.com/amitshekhariitbhu/MVVM-Architecture-Android/master/assets/mvvm-arch.png)
