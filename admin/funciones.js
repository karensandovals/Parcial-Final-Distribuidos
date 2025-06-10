let areaSeleccionada = null;
let stompClient = null;
let ultimoCodigoEstudiante = null;


function conectarArea() {
  areaSeleccionada = document.getElementById('selectArea').value;

  const socket = new SockJS('http://localhost:5004/ws');
  stompClient = Stomp.over(socket);

  stompClient.connect({}, function () {
    stompClient.subscribe(`/notificacion/general/${areaSeleccionada}`, function (mensaje) {
      mostrarMensaje(mensaje.body);
    });

    stompClient.subscribe(`/notificacion/${areaSeleccionada}`, function (mensaje) {
      mostrarDeudas(JSON.parse(mensaje.body));
    });

    document.getElementById('btnConectar').disabled = true;
    document.getElementById('btnDesconectar').disabled = false; 
    document.getElementById('btnEliminarDeudas').disabled = false; // Habilita 
  });
}

function desconectarArea() {
  if (stompClient) {
    stompClient.disconnect(() => {
      mostrarMensaje("Desconectado.");
      document.getElementById('btnConectar').disabled = false;
      document.getElementById('btnDesconectar').disabled = true;
      document.getElementById('deudas').innerHTML = '';
      document.getElementById('btnEliminarDeudas').disabled = true; // Deshabilita 
      stompClient = null;
    }, {});
  }
}

function mostrarDeudas(data) {
  const contenedor = document.getElementById('deudas');
  const botonEliminar = document.getElementById('btnEliminarDeudas');
  contenedor.innerHTML = '';

  if (!data || data.length === 0) {
    contenedor.innerHTML = `
      <div class="alert alert-success" role="alert">
        ✅ El estudiante está <strong>a paz y salvo</strong> con el área de <strong>${areaSeleccionada}</strong>.
      </div>
    `;
    botonEliminar.style.display = 'none';
    ultimoCodigoEstudiante = null;
    return;
  }

  ultimoCodigoEstudiante = data[0].codigoEstudiante;

  contenedor.innerHTML = `
    <div class="alert alert-danger" role="alert">
      ⚠️ El estudiante tiene pendientes con el área de <strong>${areaSeleccionada}</strong>.
    </div>
  `;

  let tabla = `
    <div class="table-responsive">
      <table class="table table-bordered table-striped table-sm align-middle">
        <thead class="table-dark">
          <tr>
            <th>Código</th>
            ${areaSeleccionada === 'financiera' ? `
              <th>Monto Adeudado</th>
              <th>Motivo</th>
              <th>Fecha Generación</th>
              <th>Fecha Límite</th>
              <th>Estado</th>
            ` : areaSeleccionada === 'laboratorio' ? `
              <th>Equipo Prestado</th>
              <th>Estado</th>
              <th>Fecha Préstamo</th>
              <th>Fecha Estimada</th>
              <th>Fecha Real</th>
            ` : `
              <th>Implemento</th>
              <th>Fecha Préstamo</th>
              <th>Fecha Estimada</th>
              <th>Fecha Real</th>
            `}
          </tr>
        </thead>
        <tbody>
  `;

  data.forEach(item => {
    tabla += "<tr>";
    tabla += `<td>${item.codigoEstudiante}</td>`;
    if (areaSeleccionada === 'financiera') {
      tabla += `
        <td>$${item.montoAdeudado}</td>
        <td>${item.motivoDeuda}</td>
        <td>${item.fechaGeneracionDeuda}</td>
        <td>${item.fechaLimitePago}</td>
        <td>${item.estadoDeuda}</td>
      `;
    } else if (areaSeleccionada === 'laboratorio') {
      tabla += `
        <td>${item.equipoPrestado}</td>
        <td>${item.estadoPrestamo}</td>
        <td>${item.fechaPrestamo}</td>
        <td>${item.fechaDevolucionEstimada}</td>
        <td>${item.fechaDevolucionReal ?? "—"}</td>
      `;
    } else {
      tabla += `
        <td>${item.implementoDeportivoPrestado}</td>
        <td>${item.fechaPrestamo}</td>
        <td>${item.fechaDevolucionEstimada}</td>
        <td>${item.fechaDevolucionReal ?? "—"}</td>
      `;
    }
    tabla += "</tr>";
  });

  tabla += "</tbody></table></div>";
  contenedor.innerHTML += tabla;
  botonEliminar.style.display = 'inline-block';
}

function eliminarDeudas() {
  if (!areaSeleccionada) {
    alert("Primero selecciona un área.");
    return;
  }

  if (!ultimoCodigoEstudiante) {
    alert("No hay código de estudiante registrado. Espera a que llegue una notificación.");
    return;
  }

  const peticion = {
    codigoEstudiante: ultimoCodigoEstudiante
  };

  let endpoint = '';
  switch (areaSeleccionada) {
    case 'financiera':
      endpoint = '/api/eliminarDeudasFinanciera';
      break;
    case 'laboratorio':
      endpoint = '/api/eliminarDeudasLaboratorio';
      break;
    case 'deportes':
      endpoint = '/api/eliminarDeudasDeportes';
      break;
    default:
      alert("Área no válida.");
      return;
  }

  fetch(`http://localhost:5004${endpoint}`, {
    method: 'DELETE',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(peticion)
  })
    .then(response => {
      if (!response.ok) {
        throw new Error("Error al eliminar la deuda.");
      }
      return response.text();
    })
    .then(msg => {
      mostrarMensaje(msg);
      document.getElementById('deudas').innerHTML = '';
      ultimoCodigoEstudiante = null; // Limpiar después de eliminar
    })
    .catch(error => {
      console.error(error);
      alert("Hubo un problema al eliminar la deuda.");
    });
}

function mostrarMensaje(msg) {
  const contenedor = document.getElementById('mensajes');
  contenedor.innerHTML = `
    <div class="alert alert-info d-flex align-items-center" role="alert">
      <i class="fa-solid fa-circle-info me-2"></i>
      <div><strong>Notificación:</strong> ${msg}</div>
    </div>
  `;
}


