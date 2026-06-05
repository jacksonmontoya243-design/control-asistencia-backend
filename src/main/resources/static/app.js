const API = "http://localhost:8080/api/empleados";

let empleadoEditando = null;

async function cargarEmpleados() {

    const respuesta = await fetch(API);

    const empleados = await respuesta.json();

    const tabla = document.getElementById("tablaEmpleados");

    tabla.innerHTML = "";

    let administradores = 0;
    let supervisores = 0;

    empleados.forEach(empleado => {

        if (empleado.cargo.toLowerCase() === "administrador") {
            administradores++;
        }

        if (empleado.cargo.toLowerCase() === "supervisor") {
            supervisores++;
        }

        tabla.innerHTML += `
            <tr>
                <td>${empleado.id}</td>
                <td>${empleado.nombre}</td>
                <td>${empleado.documento}</td>
                <td>${empleado.cargo}</td>

                <td class="acciones">

    <button
        class="editar-btn"
        onclick="editarEmpleado(
            ${empleado.id},
            '${empleado.nombre}',
            '${empleado.documento}',
            '${empleado.cargo}'
        )"
    >
        <i class="fa-solid fa-pen"></i>
        Editar
    </button>

    <button
        class="eliminar-btn"
        onclick="eliminarEmpleado(${empleado.id})"
    >
        <i class="fa-solid fa-trash"></i>
        Eliminar
    </button>

    <button
        class="qr-btn"
        onclick="verQR(${empleado.id})"
    >
        <i class="fa-solid fa-qrcode"></i>
        Ver QR
    </button>

</td>
            </tr>
        `;
    });

    document.getElementById("totalEmpleados").innerText = empleados.length;
    document.getElementById("totalAdministradores").innerText = administradores;
    document.getElementById("totalSupervisores").innerText = supervisores;
}

async function guardarEmpleado() {

    const nombre = document.getElementById("nombre").value;
    const documento = document.getElementById("documento").value;
    const cargo = document.getElementById("cargo").value;

    const url = empleadoEditando
        ? `${API}/${empleadoEditando}`
        : API;

    const metodo = empleadoEditando
        ? "PUT"
        : "POST";

    await fetch(url, {
        method: metodo,
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            nombre,
            documento,
            cargo
        })
    });

    document.getElementById("nombre").value = "";
    document.getElementById("documento").value = "";
    document.getElementById("cargo").value = "";

    empleadoEditando = null;

    cargarEmpleados();
}

async function eliminarEmpleado(id) {

    await fetch(`${API}/${id}`, {
        method: "DELETE"
    });

    cargarEmpleados();
}

function editarEmpleado(id, nombre, documento, cargo) {

    document.getElementById("nombre").value = nombre;
    document.getElementById("documento").value = documento;
    document.getElementById("cargo").value = cargo;

    empleadoEditando = id;
}

function buscarEmpleado() {

    const texto = document
        .getElementById("buscar")
        .value
        .toLowerCase();

    const filas = document.querySelectorAll("#tablaEmpleados tr");

    filas.forEach(fila => {

        const contenido = fila.innerText.toLowerCase();

        fila.style.display =
            contenido.includes(texto)
                ? ""
                : "none";
    });
}

cargarEmpleados();
function verQR(id) {

    window.open(
        `/qr/empleado_${id}.png`,
        "_blank"
    );
}