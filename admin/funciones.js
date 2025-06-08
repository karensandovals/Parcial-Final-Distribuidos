let clienteNotificaciones = null;

function conectarAdministrador(area) {
  const socket = new SockJS('http://localhost:5000/ws');
  clienteNotificaciones = Stomp.over(socket);

  clienteNotificaciones.connect({}, function (frame) {
    console.log(`Conectado como administrador del área: ${area}`);
    clienteNotificaciones.subscribe(`/notificacion/${area}`, function (mensaje) {
      const data = JSON.parse(mensaje.body);
      mostrarDeudas(area, data);
    });
  });
}

function mostrarDeudas(area, data) {
  const contenedor = document.getElementById(`deudas-${area}`);
  contenedor.innerHTML = ''; // Limpiar contenido anterior

  if (!Array.isArray(data) || data.length === 0) {
    contenedor.innerHTML = '<p>No hay deudas registradas.</p>';
    return;
  }

  data.forEach(item => {
    const parrafo = document.createElement('p');
    if (area === 'financiera') {
      parrafo.textContent = `Estudiante ${item.codigoEstudiante} debe $${item.valorDeuda} por concepto de ${item.concepto}.`;
    } else if (area === 'laboratorio') {
      parrafo.textContent = `Estudiante ${item.codigoEstudiante} tiene pendiente el laboratorio: ${item.nombreLaboratorio}.`;
    } else if (area === 'deportes') {
      parrafo.textContent = `Estudiante ${item.codigoEstudiante} debe devolver el implemento: ${item.nombreElemento}.`;
    }
    contenedor.appendChild(parrafo);
  });
}
