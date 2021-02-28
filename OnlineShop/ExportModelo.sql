

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Base de datos: `nameBD`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `Carritos`
--

CREATE TABLE IF NOT EXISTS nameBD.`Carritos` (
  `CodigoCarrito` varchar(36) collate utf8_spanish2_ci NOT NULL,
  `CodigoProducto` varchar(36) collate utf8_spanish2_ci NOT NULL,
  `Nombre` varchar(70) collate utf8_spanish2_ci NOT NULL,
  `Precio` double NOT NULL,
  `Cantidad` int(11) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_spanish2_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `Comentarios`
--

CREATE TABLE IF NOT EXISTS nameBD.`Comentarios` (
  `CodigoComentario` varchar(36) collate utf8_spanish2_ci NOT NULL,
  `FechaHora` datetime NOT NULL,
  `CodigoProducto` varchar(36) collate utf8_spanish2_ci NOT NULL,
  `Email` varchar(60) collate utf8_spanish2_ci NOT NULL,
  `Nombre` varchar(100) collate utf8_spanish2_ci NOT NULL,
  `Comentario` text collate utf8_spanish2_ci NOT NULL,
  PRIMARY KEY  (`CodigoComentario`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_spanish2_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `HistorialCarritos`
--

CREATE TABLE IF NOT EXISTS nameBD.`HistorialCarritos` (
  `Email` varchar(60) collate utf8_spanish2_ci NOT NULL,
  `CodigoCarrito` varchar(36) collate utf8_spanish2_ci NOT NULL,
  `FechaHora` datetime NOT NULL,
  `Precio` double NOT NULL,
  `Pago` varchar(30) collate utf8_spanish2_ci default NULL,
  `Completado` tinyint(1) NOT NULL,
  PRIMARY KEY  (`CodigoCarrito`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_spanish2_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `Log`
--

CREATE TABLE IF NOT EXISTS nameBD.`Log` (
  `FechaHora` datetime NOT NULL,
  `Url` varchar(255) collate utf8_spanish2_ci NOT NULL,
  `RemoteIP` varchar(32) collate utf8_spanish2_ci NOT NULL,
  `RemoteHost` varchar(255) collate utf8_spanish2_ci NOT NULL,
  `Method` varchar(10) collate utf8_spanish2_ci NOT NULL,
  `Param` text collate utf8_spanish2_ci,
  `User-Agent` varchar(255) collate utf8_spanish2_ci default NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_spanish2_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `Productos`
--

CREATE TABLE IF NOT EXISTS nameBD.`Productos` (
  `Codigo` varchar(36) collate utf8_spanish2_ci NOT NULL,
  `Nombre` varchar(70) collate utf8_spanish2_ci NOT NULL,
  `Precio` double NOT NULL,
  `Stock` int(11) NOT NULL,
  `Descripcion` varchar(100) collate utf8_spanish2_ci NOT NULL,
  `Detalles` text collate utf8_spanish2_ci NOT NULL,
  PRIMARY KEY  (`Codigo`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_spanish2_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `Usuarios`
--

CREATE TABLE IF NOT EXISTS nameBD.`Usuarios` (
  `Email` varchar(60) collate utf8_spanish2_ci NOT NULL,
  `Nombre` varchar(100) collate utf8_spanish2_ci NOT NULL,
  `Direccion` varchar(200) collate utf8_spanish2_ci NOT NULL,
  `Pass` varchar(32) collate utf8_spanish2_ci NOT NULL,
  `Permisos` char(1) collate utf8_spanish2_ci NOT NULL,
  PRIMARY KEY  (`Email`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_spanish2_ci;
