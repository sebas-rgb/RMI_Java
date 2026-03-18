# 📦 RMI Java - Sistema Distribuido

## 📌 Descripción

Este proyecto implementa un sistema distribuido utilizando **Java RMI (Remote Method Invocation)**, permitiendo la comunicación entre un cliente y un servidor mediante la invocación de métodos remotos.

El sistema está basado en una arquitectura cliente-servidor, donde el servidor expone servicios remotos y el cliente los consume como si fueran métodos locales.

---

## 🧠 Objetivo del Proyecto

Simular un sistema distribuido en Java que permita:

- Comunicación remota entre procesos
- Invocación de métodos desde un cliente hacia un servidor
- Uso de interfaces remotas
- Comprensión del funcionamiento de RMI

---

## ⚙️ Tecnologías Utilizadas

- Java
- Java RMI
- JDK
- Programación Orientada a Objetos
- Arquitectura Cliente-Servidor

---

## 🏗️ Arquitectura del Sistema

El sistema se compone de tres partes principales:

### 1. Interfaz Remota
Define los métodos que pueden ser invocados remotamente.

### 2. Servidor
- Implementa la interfaz remota  
- Registra el objeto en el `rmiregistry`  
- Atiende solicitudes del cliente  

### 3. Cliente
- Busca el objeto remoto  
- Invoca métodos del servidor  

---

## 🔄 Flujo de Funcionamiento

1. Se inicia el `rmiregistry`  
2. Se ejecuta el servidor y registra el objeto  
3. El cliente busca el servicio remoto  
4. El cliente invoca métodos  
5. El servidor responde  

---

## 📂 Estructura del Proyecto
