let clienteNotificaciones = null;
let conexionesPorArea = {};

function conectarAdministrador(area) {
  if (conexionesPorArea[area]) {
    console.warn(`Ya está conectado al área: ${area}`);
    return;
  }

  const socket = new SockJS('http://localhost:5004/ws');
  const stomp = Stomp.over(socket);
  clienteNotificaciones = stomp;

  stomp.connect({}, function () {
    console.log(`Conectado como administrador del área: ${area}`);
    conexionesPorArea[area] = stomp;

    stomp.subscribe(`/notificacion/${area}`, function (mensaje) {
      const data = JSON.parse(mensaje.body);
      mostrarDeudas(area, data);
    });

    actualizarEstadoBotones(area, true);
  });
}

function desconectarAdministrador(area) {
  const conexion = conexionesPorArea[area];
  if (conexion) {
    conexion.disconnect(() => {
      console.log(`Desconectado del área: ${area}`);
      delete conexionesPorArea[area];
      actualizarEstadoBotones(area, false);
      limpiarDeudas(area);
    });
  }
}

function mostrarDeudas(area, data) {
  const contenedor = document.getElementById(`deudas-${area}`);
  contenedor.innerHTML = '';

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

function limpiarDeudas(area) {
  const contenedor = document.getElementById(`deudas-${area}`);
  contenedor.innerHTML = '<p>Desconectado.</p>';
}

function actualizarEstadoBotones(area, conectado) {
  document.getElementById(`btnConectar-${area}`).disabled = conectado;
  document.getElementById(`btnDesconectar-${area}`).disabled = !conectado;
}
