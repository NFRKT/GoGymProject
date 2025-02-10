-- phpMyAdmin SQL Dump
-- version 5.2.0
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Czas generowania: 10 Lut 2025, 02:59
-- Wersja serwera: 10.4.27-MariaDB
-- Wersja PHP: 8.2.0

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Baza danych: `gogym`
--

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `body_part`
--

CREATE TABLE `body_part` (
  `id_bodypart` int(11) NOT NULL,
  `name` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_polish_ci;

--
-- Zrzut danych tabeli `body_part`
--

INSERT INTO `body_part` (`id_bodypart`, `name`) VALUES
(1, 'Chest'),
(2, 'Back'),
(3, 'Shoulders'),
(4, 'Biceps'),
(5, 'Triceps'),
(6, 'Forearms'),
(7, 'Abs'),
(8, 'Quads'),
(9, 'Hamstrings'),
(10, 'Glutes'),
(11, 'Calves'),
(12, 'Neck'),
(13, 'Full Body'),
(14, 'Cardio');

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `client_details`
--

CREATE TABLE `client_details` (
  `id` bigint(20) NOT NULL,
  `id_user` int(11) NOT NULL,
  `weight` double DEFAULT NULL,
  `height` double DEFAULT NULL,
  `waist` double DEFAULT NULL,
  `hips` double DEFAULT NULL,
  `chest` double DEFAULT NULL,
  `profile_picture` varchar(255) DEFAULT '/images/default-profile.png',
  `phone_number` varchar(20) NOT NULL,
  `city` varchar(50) NOT NULL,
  `update_date` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_polish_ci;

--
-- Zrzut danych tabeli `client_details`
--

INSERT INTO `client_details` (`id`, `id_user`, `weight`, `height`, `waist`, `hips`, `chest`, `profile_picture`, `phone_number`, `city`, `update_date`) VALUES
(1, 5, 91.6, 191, 80.5, 80.3, 80.5, NULL, '000000003', 'Rybnik', '2025-02-10');

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `equipment`
--

CREATE TABLE `equipment` (
  `id_equipment` int(11) NOT NULL,
  `name` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_polish_ci;

--
-- Zrzut danych tabeli `equipment`
--

INSERT INTO `equipment` (`id_equipment`, `name`) VALUES
(1, 'Barbell'),
(2, 'Dumbbell'),
(3, 'Kettlebell'),
(4, 'Band'),
(5, 'Body Weight'),
(6, 'Pull-Up Bar'),
(7, 'Bench'),
(8, 'Cardio Machine'),
(9, 'Yoga Mat'),
(10, 'Medicine Ball'),
(11, 'Stability Ball'),
(12, 'Foam Roller'),
(13, 'Plyometric Box'),
(14, 'Cable Machine'),
(15, 'Strength Machine'),
(16, 'Jump Rope'),
(17, 'EZ Curl Bar'),
(18, 'Dip Bars'),
(19, 'Weighted Vest');

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `exercise`
--

CREATE TABLE `exercise` (
  `id_exercise` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  `description` text NOT NULL,
  `difficulty` enum('beginner','intermediate','advanced') NOT NULL,
  `jpg` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_polish_ci;

--
-- Zrzut danych tabeli `exercise`
--

INSERT INTO `exercise` (`id_exercise`, `name`, `description`, `difficulty`, `jpg`) VALUES
(1, 'Push-Up', 'Klasyczne pompki angażujące mięśnie klatki piersiowej, ramion i core.', 'beginner', '/images/pushup.jpg'),
(2, 'Bodyweight Squat', 'Przysiady z wykorzystaniem masy własnego ciała, wzmacniające mięśnie nóg i pośladków.', 'beginner', '/images/pushup.jpg'),
(3, 'Plank', 'Izometryczne ćwiczenie wzmacniające mięśnie core, pleców i ramion.', 'beginner', '/images/pushup.jpg'),
(4, 'Dumbbell Curl', 'Uginanie przedramion z hantlami, ukierunkowane na bicepsy.', 'beginner', '/images/pushup.jpg'),
(5, 'Lat Pulldown', 'Ćwiczenie na maszynie angażujące mięśnie pleców poprzez przyciąganie drążka do klatki piersiowej.', 'beginner', '/images/pushup.jpg'),
(6, 'Leg Press', 'Przysiady na maszynie do wyciskania nogami, idealne na mięśnie nóg.', 'beginner', '/images/pushup.jpg'),
(7, 'Seated Row', 'Wiosłowanie na maszynie, wzmacniające plecy i ramiona.', 'beginner', '/images/pushup.jpg'),
(8, 'Incline Walk', 'Marsz na bieżni pod kątem, idealny dla poprawy kondycji i spalania kalorii.', 'beginner', '/images/pushup.jpg'),
(9, 'Kettlebell Deadlift', 'Martwy ciąg z odważnikiem kettlebell, wzmacniający mięśnie nóg, pośladków i pleców.', 'beginner', '/images/pushup.jpg'),
(10, 'Chest Press (Machine)', 'Wyciskanie na maszynie wzmacniające mięśnie klatki piersiowej i ramion.', 'beginner', '/images/pushup.jpg'),
(11, 'Leg Curl (Machine)', 'Uginanie nóg na maszynie, wzmacniające mięśnie dwugłowe uda.', 'beginner', '/images/pushup.jpg'),
(12, 'Arm Curl (Machine)', 'Uginanie ramion na maszynie, ukierunkowane na bicepsy.', 'beginner', '/images/pushup.jpg'),
(13, 'Shoulder Press (Machine)', 'Wyciskanie na maszynie wzmacniające mięśnie naramienne.', 'beginner', '/images/pushup.jpg'),
(14, 'Tricep Dips (Bench)', 'Ćwiczenie na tricepsy wykonywane z pomocą ławki.', 'beginner', '/images/pushup.jpg'),
(15, 'Incline Dumbbell Fly', 'Rozpiętki na ławce skośnej, wzmacniające klatkę piersiową.', 'beginner', '/images/pushup.jpg'),
(16, 'Step-Ups', 'Wejścia na podwyższenie, angażujące nogi i pośladki.', 'beginner', '/images/pushup.jpg'),
(17, 'Wall Sit', 'Izometryczne ćwiczenie nóg polegające na „siedzeniu” przy ścianie.', 'beginner', '/images/pushup.jpg'),
(18, 'Side Plank', 'Plank boczny wzmacniający mięśnie skośne brzucha i core.', 'beginner', '/images/pushup.jpg'),
(19, 'Cable Chest Fly', 'Rozpiętki na wyciągu, angażujące klatkę piersiową.', 'beginner', '/images/pushup.jpg'),
(20, 'Treadmill Jogging', 'Lekki bieg na bieżni, poprawiający kondycję i spalanie kalorii.', 'beginner', '/images/pushup.jpg'),
(21, 'Pull-Up', 'Podciąganie na drążku, angażujące plecy, bicepsy i core.', 'intermediate', '/images/pushup.jpg'),
(22, 'Barbell Squat', 'Przysiady ze sztangą, kompleksowe ćwiczenie na nogi, pośladki i core.', 'intermediate', '/images/pushup.jpg'),
(23, 'Deadlift', 'Martwy ciąg ze sztangą, ćwiczenie na plecy, nogi i pośladki.', 'intermediate', '/images/pushup.jpg'),
(24, 'Dumbbell Bench Press', 'Wyciskanie hantli na ławce, angażujące klatkę piersiową i tricepsy.', 'intermediate', '/images/pushup.jpg'),
(25, 'Cable Tricep Pushdown', 'Prostowanie ramion na wyciągu, ćwiczenie na tricepsy.', 'intermediate', '/images/pushup.jpg'),
(26, 'Overhead Shoulder Press (Machine)', 'Wyciskanie nad głowę na maszynie, wzmacniające barki.', 'intermediate', '/images/pushup.jpg'),
(27, 'Romanian Deadlift', 'Martwy ciąg na prostych nogach z hantlami lub sztangą, wzmacniający pośladki i mięśnie dwugłowe uda.', 'intermediate', '/images/pushup.jpg'),
(28, 'Lunges', 'Wykroki z hantlami, angażujące nogi i pośladki.', 'intermediate', '/images/pushup.jpg'),
(29, 'Russian Twist', 'Skręty tułowia z obciążeniem, wzmacniające mięśnie skośne brzucha.', 'intermediate', '/images/pushup.jpg'),
(30, 'Battle Ropes', 'Dynamiczne ruchy linami angażujące całe ciało i poprawiające kondycję.', 'intermediate', '/images/pushup.jpg'),
(31, 'Goblet Squat', 'Przysiad z kettlebellem trzymanym przy klatce piersiowej.', 'intermediate', '/images/pushup.jpg'),
(32, 'One-Arm Dumbbell Row', 'Wiosłowanie jednorącz z hantlą, wzmacniające plecy i ramiona.', 'intermediate', '/images/pushup.jpg'),
(33, 'Incline Bench Press', 'Wyciskanie sztangi na ławce skośnej, ukierunkowane na górną część klatki piersiowej.', 'intermediate', '/images/pushup.jpg'),
(34, 'Cable Lateral Raise', 'Unoszenie ramion na boki na wyciągu, wzmacniające barki.', 'intermediate', '/images/pushup.jpg'),
(35, 'Roman Chair Back Extension', 'Prostowanie pleców na ławce rzymskiej, wzmacniające dolną część pleców.', 'intermediate', '/images/pushup.jpg'),
(36, 'Kettlebell Swing', 'Dynamiczne ćwiczenie angażujące całe ciało, szczególnie pośladki i plecy.', 'intermediate', '/images/pushup.jpg'),
(37, 'Walking Lunges', 'Chodzone wykroki z obciążeniem lub bez, angażujące nogi i pośladki.', 'intermediate', '/images/pushup.jpg'),
(38, 'Chest Dip', 'Pompki na poręczach, angażujące klatkę piersiową i tricepsy.', 'intermediate', '/images/pushup.jpg'),
(39, 'Hammer Curl', 'Uginanie ramion z hantlami w neutralnym uchwycie, wzmacniające bicepsy i przedramiona.', 'intermediate', '/images/pushup.jpg'),
(40, 'Overhead Dumbbell Triceps Extension', 'Prostowanie ramion nad głową z hantlą, ćwiczenie na tricepsy.', 'intermediate', '/images/pushup.jpg'),
(41, 'Snatch', 'Rwanie sztangi nad głowę, ćwiczenie dynamiczne angażujące całe ciało.', 'advanced', '/images/pushup.jpg'),
(42, 'Clean and Jerk', 'Podrzut sztangi nad głowę, ćwiczenie złożone na siłę i dynamikę.', 'advanced', '/images/pushup.jpg'),
(43, 'Pistol Squat', 'Przysiady na jednej nodze, wzmacniające nogi, pośladki i poprawiające równowagę.', 'advanced', '/images/pushup.jpg'),
(44, 'Muscle-Up', 'Podciąganie z przejściem nad drążek, angażujące całe górne partie ciała.', 'advanced', '/images/pushup.jpg'),
(45, 'Bench Press', 'Wyciskanie sztangi na ławce poziomej, ćwiczenie na klatkę piersiową i ramiona.', 'advanced', '/images/pushup.jpg'),
(46, 'Bulgarian Split Squat', 'Przysiady na jednej nodze z tylną nogą na podwyższeniu, wzmacniające nogi i pośladki.', 'advanced', '/images/pushup.jpg'),
(47, 'Overhead Squat', 'Przysiad ze sztangą trzymaną nad głową, wymagający siły i stabilności.', 'advanced', '/images/pushup.jpg'),
(48, 'Front Squat', 'Przysiady ze sztangą z przodu, angażujące głównie nogi i core.', 'advanced', '/images/pushup.jpg'),
(49, 'Sled Push', 'Pchanie sanek z obciążeniem, dynamiczne ćwiczenie na nogi i pośladki.', 'advanced', '/images/pushup.jpg'),
(50, 'Rope Climb', 'Wspinanie się po linie, wymagające siły ramion, pleców i core.', 'advanced', '/images/pushup.jpg'),
(51, 'Turkish Get-Up', 'Wstawanie z pozycji leżącej z kettlebellem, ćwiczenie na całe ciało i stabilność.', 'advanced', '/images/pushup.jpg'),
(52, 'Farmers Walk', 'Spacer z ciężarami, angażujący całe ciało i poprawiający chwyt.', 'advanced', '/images/pushup.jpg'),
(53, 'Barbell Hip Thrust', 'Wypychanie bioder ze sztangą, ćwiczenie na pośladki.', 'advanced', '/images/pushup.jpg'),
(54, 'Barbell Overhead Press', 'Wyciskanie sztangi nad głowę, ćwiczenie na barki i core.', 'advanced', '/images/pushup.jpg'),
(55, 'Good Morning', 'Skłony ze sztangą, wzmacniające plecy i mięśnie dwugłowe uda.', 'advanced', '/images/pushup.jpg'),
(56, 'Barbell Front Lunge', 'Wykroki z przodu z obciążeniem, wzmacniające nogi i pośladki.', 'advanced', '/images/pushup.jpg'),
(57, 'Windshield Wipers', 'Ćwiczenie na mięśnie brzucha polegające na skrętach nóg w leżeniu.', 'advanced', '/images/pushup.jpg'),
(58, 'Hanging Leg Raise', 'Podnoszenie nóg w zwisie na drążku, wzmacniające mięśnie brzucha i biodra.', 'advanced', '/images/pushup.jpg'),
(59, 'Single-Leg Deadlift', 'Martwy ciąg na jednej nodze z hantlami lub sztangą, wymagający równowagi i siły.', 'advanced', '/images/pushup.jpg'),
(60, 'Clean Pull', 'Pociągnięcie sztangi w ruchu przygotowującym do rwania, ćwiczenie na siłę i dynamikę.', 'advanced', '/images/pushup.jpg'),
(61, 'Dumbbell Shoulder Press', 'Wyciskanie hantli nad głową, angażujące mięśnie naramienne i górną część klatki piersiowej.', 'beginner', '/images/pushup.jpg'),
(62, 'Standing Calf Raise (Machine)', 'Wzmacnianie mięśni łydek przy użyciu maszyny do wznosów.', 'beginner', '/images/pushup.jpg'),
(63, 'Leg Extension (Machine)', 'Rozciąganie nóg na maszynie, angażujące mięśnie czworogłowe uda.', 'beginner', '/images/pushup.jpg'),
(64, 'Bent Over Dumbbell Fly', 'Unoszenie hantli w opadzie, wzmacniające tylne partie barków i pleców.', 'beginner', '/images/pushup.jpg'),
(65, 'Seated Leg Press', 'Ćwiczenie nóg na maszynie do wyciskania, wzmacniające uda i pośladki.', 'beginner', '/images/pushup.jpg'),
(66, 'Mountain Climbers', 'Ćwiczenie dynamiczne angażujące core, nogi i ramiona, przypominające wspinaczkę.', 'beginner', '/images/pushup.jpg'),
(67, 'Bird-Dog', 'Ćwiczenie na równowagę i stabilność, angażujące mięśnie pleców i brzucha.', 'beginner', '/images/pushup.jpg'),
(68, 'Glute Bridge', 'Wzmacnianie pośladków i dolnej części pleców, leżąc na plecach i unosząc biodra.', 'beginner', '/images/pushup.jpg'),
(69, 'Seated Row (Machine)', 'Wiosłowanie na maszynie, angażujące mięśnie pleców, ramion i core.', 'beginner', '/images/pushup.jpg'),
(70, 'Treadmill Walking', 'Marsz na bieżni, idealny dla poprawy kondycji i spalania tłuszczu.', 'beginner', '/images/pushup.jpg'),
(71, 'Reverse Lunge', 'Wykrok wstecz, angażujący mięśnie nóg i pośladków, może być wykonywany z hantlami lub sztangą.', 'intermediate', '/images/pushup.jpg'),
(72, 'Dumbbell Shoulder Raise', 'Unoszenie hantli na boki w celu wzmocnienia mięśni naramiennych.', 'intermediate', '/images/pushup.jpg'),
(73, 'Chest Fly (Machine)', 'Rozpiętki na maszynie, angażujące głównie mięśnie klatki piersiowej.', 'intermediate', '/images/pushup.jpg'),
(74, 'Cable Row', 'Wiosłowanie na wyciągu, angażujące plecy, ramiona i core.', 'intermediate', '/images/pushup.jpg'),
(75, 'Leg Press Calf Raise', 'Wzmacnianie mięśni łydek przy użyciu maszyny do wyciskania nóg.', 'intermediate', '/images/pushup.jpg'),
(76, 'Russian Twist with Weight', 'Skręty tułowia z obciążeniem, angażujące mięśnie brzucha i skośne.', 'intermediate', '/images/pushup.jpg'),
(77, 'Dumbbell Shrug', 'Unoszenie barków z hantlami, wzmacniające mięśnie czworoboczne.', 'intermediate', '/images/pushup.jpg'),
(78, 'Machine Chest Press', 'Wyciskanie na maszynie, wzmacniające mięśnie klatki piersiowej i tricepsy.', 'intermediate', '/images/pushup.jpg'),
(79, 'Concentration Curl', 'Izolowane uginanie ramienia z hantlem, ukierunkowane na bicepsy.', 'intermediate', '/images/pushup.jpg'),
(80, 'Box Jump', 'Wyskok na podwyższenie, doskonały na poprawę dynamiki i siły nóg.', 'intermediate', '/images/pushup.jpg'),
(81, 'Barbell Clean and Press', 'Podrzut i wyciskanie sztangi nad głowę, ćwiczenie na siłę i dynamikę.', 'advanced', '/images/pushup.jpg'),
(82, 'Plyometric Push-Up', 'Pompki z dynamicznym odbiciem, angażujące mięśnie klatki piersiowej, ramion i core.', 'advanced', '/images/pushup.jpg'),
(83, 'Barbell Bulgarian Split Squat', 'Przysiady na jednej nodze ze sztangą na plecach, angażujące nogi i pośladki.', 'advanced', '/images/pushup.jpg'),
(84, 'Barbell Bent Over Row', 'Wiosłowanie ze sztangą w opadzie, wzmacniające plecy i ramiona.', 'advanced', '/images/pushup.jpg'),
(85, 'Jump Squat', 'Przysiad z wyskokiem, angażujący mięśnie nóg, pośladków i poprawiający dynamikę.', 'advanced', '/images/pushup.jpg'),
(86, 'Handstand Push-Up', 'Pompki przy ścianie w staniu na rękach, wymagające siły ramion i stabilności.', 'advanced', '/images/pushup.jpg'),
(87, 'Dumbbell Renegade Row', 'Wiosłowanie z hantlami w pozycji pompki, angażujące plecy, ramiona i core.', 'advanced', '/images/pushup.jpg'),
(88, 'Front Raise with Plate', 'Unoszenie talerza sztangi przed sobą, wzmacniające mięśnie barków.', 'advanced', '/images/pushup.jpg'),
(89, 'Pistol Squat to Box', 'Przysiad na jednej nodze z pomocą podwyższenia, angażujący nogi, pośladki i równowagę.', 'advanced', '/images/pushup.jpg'),
(90, 'Tire Flip', 'Przewracanie opony, ćwiczenie siłowe angażujące całe ciało.', 'advanced', '/images/pushup.jpg');

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `exercise_bodypart`
--

CREATE TABLE `exercise_bodypart` (
  `id_exercise` int(11) NOT NULL,
  `id_bodypart` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_polish_ci;

--
-- Zrzut danych tabeli `exercise_bodypart`
--

INSERT INTO `exercise_bodypart` (`id_exercise`, `id_bodypart`) VALUES
(1, 1),
(1, 3),
(1, 7),
(2, 8),
(2, 10),
(3, 7),
(4, 4),
(5, 2),
(5, 4),
(6, 8),
(6, 10),
(7, 2),
(7, 4),
(8, 14),
(9, 2),
(9, 8),
(9, 10),
(10, 1),
(10, 3),
(11, 9),
(12, 4),
(13, 3),
(14, 5),
(15, 1),
(16, 8),
(16, 10),
(17, 8),
(17, 10),
(18, 7),
(19, 1),
(20, 14),
(21, 2),
(21, 4),
(22, 1),
(22, 3),
(23, 4),
(24, 8),
(24, 10),
(25, 7),
(26, 14),
(27, 1),
(28, 1),
(28, 2),
(29, 5),
(30, 8),
(31, 4),
(32, 2),
(32, 8),
(32, 10),
(33, 1),
(33, 3),
(34, 2),
(34, 14),
(35, 7),
(36, 9),
(36, 10),
(37, 5),
(38, 4),
(39, 8),
(39, 10),
(40, 7),
(41, 2),
(41, 3),
(42, 9),
(42, 10),
(43, 1),
(43, 3),
(44, 8),
(44, 10),
(45, 5),
(46, 2),
(47, 4),
(48, 7),
(49, 5),
(50, 14),
(51, 5),
(52, 9),
(52, 10),
(53, 1),
(53, 3),
(54, 7),
(55, 6),
(55, 9),
(56, 1),
(56, 3),
(57, 4),
(57, 10),
(58, 2),
(59, 8),
(59, 10),
(60, 11),
(61, 9),
(61, 10),
(62, 1),
(62, 3),
(63, 2),
(64, 8),
(64, 9),
(65, 4),
(66, 1),
(66, 3),
(67, 5),
(68, 6),
(68, 9),
(69, 3),
(70, 4),
(71, 8),
(71, 10),
(72, 7),
(73, 5),
(74, 6),
(74, 10),
(75, 2),
(76, 1),
(77, 6),
(77, 9),
(78, 11),
(79, 5),
(80, 9),
(81, 1),
(81, 3),
(82, 6),
(83, 2),
(83, 10),
(84, 9),
(85, 4),
(86, 7),
(87, 8),
(88, 1),
(89, 9),
(90, 5);

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `exercise_equipment`
--

CREATE TABLE `exercise_equipment` (
  `id_exercise` int(11) NOT NULL,
  `id_equipment` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_polish_ci;

--
-- Zrzut danych tabeli `exercise_equipment`
--

INSERT INTO `exercise_equipment` (`id_exercise`, `id_equipment`) VALUES
(1, 5),
(2, 5),
(3, 5),
(4, 2),
(5, 15),
(6, 15),
(7, 15),
(8, 8),
(9, 3),
(10, 15),
(11, 15),
(12, 15),
(13, 15),
(14, 7),
(15, 2),
(15, 7),
(16, 13),
(17, 5),
(18, 5),
(19, 14),
(20, 8),
(21, 6),
(22, 2),
(22, 7),
(23, 1),
(24, 2),
(24, 7),
(25, 3),
(25, 5),
(26, 8),
(27, 15),
(28, 2),
(29, 5),
(30, 5),
(31, 14),
(32, 1),
(33, 1),
(33, 7),
(34, 8),
(35, 9),
(36, 1),
(37, 5),
(38, 2),
(39, 2),
(40, 6),
(41, 14),
(42, 1),
(42, 7),
(43, 1),
(43, 7),
(44, 2),
(44, 7),
(45, 5),
(46, 13),
(47, 2),
(48, 5),
(49, 5),
(50, 8),
(51, 5),
(52, 1),
(53, 2),
(54, 5),
(55, 1),
(55, 7),
(56, 5),
(57, 5),
(58, 6),
(59, 2),
(60, 3),
(61, 2),
(61, 9),
(62, 1),
(62, 6),
(63, 6),
(64, 4),
(65, 6),
(66, 2),
(66, 6),
(67, 5),
(68, 1),
(69, 2),
(70, 6),
(71, 2),
(72, 6),
(73, 5),
(74, 1),
(75, 6),
(76, 2),
(77, 1),
(78, 3),
(79, 5),
(80, 2),
(81, 5),
(82, 6),
(83, 1),
(83, 2),
(84, 4),
(85, 2),
(86, 5),
(87, 4),
(88, 2),
(89, 5),
(90, 5);

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `notifications`
--

CREATE TABLE `notifications` (
  `id_notification` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `message` text NOT NULL,
  `read` tinyint(1) NOT NULL DEFAULT 0,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_polish_ci;

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `plan_exercises`
--

CREATE TABLE `plan_exercises` (
  `id` int(11) NOT NULL,
  `id_plan` int(11) NOT NULL,
  `id_exercise` int(11) NOT NULL,
  `sets` int(11) NOT NULL,
  `reps` int(11) NOT NULL,
  `weight` double NOT NULL,
  `id_day` int(11) NOT NULL,
  `status` enum('notCompleted','completed') DEFAULT 'notCompleted'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_polish_ci;

--
-- Zrzut danych tabeli `plan_exercises`
--

INSERT INTO `plan_exercises` (`id`, `id_plan`, `id_exercise`, `sets`, `reps`, `weight`, `id_day`, `status`) VALUES
(28, 13, 16, 2, 1, 112, 20, 'completed'),
(29, 13, 19, 1, 1, 111, 20, 'completed'),
(30, 13, 42, 3, 3, 333, 21, 'completed'),
(34, 15, 17, 1, 1, 11, 26, 'notCompleted'),
(35, 15, 86, 1, 1, 11, 28, 'notCompleted'),
(36, 16, 18, 11, 11, 11, 29, 'notCompleted'),
(37, 16, 19, 11, 11, 11, 30, 'notCompleted'),
(38, 17, 66, 1, 1, 1, 31, 'notCompleted'),
(39, 17, 87, 11, 11, 11, 33, 'notCompleted'),
(40, 17, 18, 2, 2, 2, 33, 'notCompleted'),
(41, 17, 76, 22, 22, 22, 33, 'notCompleted'),
(42, 18, 3, 1, 1, 1, 34, 'notCompleted'),
(43, 18, 16, 1, 1, 1, 35, 'notCompleted'),
(44, 18, 16, 2, 2, 2, 35, 'notCompleted'),
(45, 18, 17, 3, 3, 3, 35, 'notCompleted'),
(46, 19, 18, 1, 1, 1, 37, 'completed'),
(47, 19, 58, 11, 11, 11, 38, 'completed'),
(48, 19, 50, 2, 2, 2, 38, 'completed'),
(49, 20, 8, 1, 1, 1, 39, 'completed'),
(50, 20, 14, 2, 2, 2, 40, 'completed'),
(51, 20, 19, 3, 3, 3, 41, 'completed'),
(52, 21, 1, 1, 1, 1, 42, 'completed'),
(53, 21, 2, 11, 11, 11, 43, 'completed'),
(54, 21, 4, 111, 111, 111, 44, 'completed'),
(55, 21, 51, 2, 2, 2, 44, 'completed'),
(56, 21, 64, 22, 22, 22, 44, 'completed'),
(57, 21, 75, 222, 222, 222, 44, 'completed'),
(58, 21, 18, 3, 3, 3, 44, 'completed'),
(59, 21, 19, 33, 33, 33, 44, 'completed'),
(60, 21, 17, 333, 333, 333, 44, 'completed'),
(62, 23, 6, 1, 1, 1, 46, 'completed'),
(63, 23, 17, 11, 11, 11, 46, 'completed'),
(64, 23, 26, 2, 2, 2, 47, 'completed'),
(65, 23, 49, 22, 22, 22, 47, 'completed'),
(66, 23, 87, 222, 222, 222, 47, 'completed'),
(67, 24, 60, 1, 1, 1, 48, 'completed'),
(68, 24, 44, 11, 11, 11, 48, 'completed'),
(69, 24, 88, 3, 3, 3, 50, 'completed'),
(70, 24, 89, 33, 33, 33, 50, 'notCompleted'),
(71, 25, 19, 1, 1, 1, 51, 'notCompleted'),
(72, 25, 53, 11, 11, 11, 51, 'notCompleted'),
(73, 25, 58, 3, 3, 3, 53, 'notCompleted'),
(74, 26, 18, 2, 2, 2, 54, 'notCompleted'),
(75, 26, 88, 3, 3, 3, 55, 'notCompleted'),
(78, 13, 1, 99, 99, 99, 20, 'completed'),
(79, 13, 20, 2, 2, 2, 73, 'completed');

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `requests`
--

CREATE TABLE `requests` (
  `id_request` int(11) NOT NULL,
  `id_trainer` int(11) NOT NULL,
  `id_client` int(11) NOT NULL,
  `status` enum('pending','accepted','rejected') DEFAULT 'pending',
  `request_date` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_polish_ci;

--
-- Zrzut danych tabeli `requests`
--

INSERT INTO `requests` (`id_request`, `id_trainer`, `id_client`, `status`, `request_date`) VALUES
(1, 3, 5, 'accepted', '2025-01-19 01:44:09'),
(2, 3, 5, 'rejected', '2025-01-19 01:46:22'),
(3, 4, 5, 'accepted', '2025-01-19 01:46:40'),
(5, 3, 2, 'accepted', '2025-01-19 19:42:23'),
(6, 3, 5, 'accepted', '2025-01-19 19:48:56'),
(7, 4, 5, 'accepted', '2025-01-19 20:05:00'),
(8, 3, 5, 'accepted', '2025-01-19 20:06:39'),
(9, 4, 5, 'accepted', '2025-01-19 22:44:15'),
(10, 4, 5, 'accepted', '2025-01-19 22:57:27'),
(12, 3, 5, 'rejected', '2025-01-19 23:14:04'),
(32, 3, 5, 'accepted', '2025-01-20 22:41:37'),
(54, 3, 5, 'accepted', '2025-01-21 12:05:08'),
(55, 4, 5, 'rejected', '2025-01-22 13:12:52'),
(56, 3, 2, 'accepted', '2025-01-23 14:18:04'),
(58, 4, 5, 'accepted', '2025-02-04 14:38:36'),
(61, 4, 5, 'accepted', '2025-02-04 14:48:02'),
(62, 4, 5, 'accepted', '2025-02-04 15:06:17'),
(63, 4, 5, 'accepted', '2025-02-04 15:08:50'),
(64, 4, 5, 'accepted', '2025-02-04 15:15:22'),
(65, 4, 5, 'rejected', '2025-02-04 15:17:23'),
(66, 4, 5, 'accepted', '2025-02-04 21:05:38'),
(67, 4, 5, 'accepted', '2025-02-04 21:15:00'),
(68, 4, 5, 'accepted', '2025-02-04 21:32:47'),
(69, 4, 5, 'accepted', '2025-02-04 21:39:44'),
(103, 4, 5, 'accepted', '2025-02-04 23:28:41'),
(105, 4, 5, 'accepted', '2025-02-04 23:39:08'),
(106, 4, 5, 'rejected', '2025-02-04 23:40:49'),
(107, 4, 5, 'rejected', '2025-02-04 23:41:19'),
(108, 4, 5, 'rejected', '2025-02-04 23:54:02'),
(110, 4, 5, 'accepted', '2025-02-04 23:54:40'),
(111, 4, 5, 'rejected', '2025-02-05 12:05:31'),
(112, 4, 5, 'accepted', '2025-02-05 12:12:08'),
(113, 4, 5, 'accepted', '2025-02-05 12:18:41'),
(114, 4, 5, 'accepted', '2025-02-05 12:23:19'),
(115, 4, 5, 'rejected', '2025-02-05 12:38:53'),
(116, 4, 5, 'rejected', '2025-02-05 12:43:23'),
(117, 4, 5, 'rejected', '2025-02-05 12:46:34'),
(118, 4, 5, 'rejected', '2025-02-05 12:50:00'),
(119, 4, 5, 'rejected', '2025-02-05 12:53:41'),
(120, 4, 5, 'accepted', '2025-02-05 12:53:48'),
(121, 4, 5, 'accepted', '2025-02-05 12:54:15'),
(122, 4, 5, 'accepted', '2025-02-05 12:54:27'),
(123, 4, 5, 'accepted', '2025-02-05 12:59:08'),
(124, 4, 5, 'rejected', '2025-02-05 12:59:26'),
(127, 4, 5, 'rejected', '2025-02-05 13:05:46'),
(128, 4, 5, 'rejected', '2025-02-05 13:06:26');

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `trainer_details`
--

CREATE TABLE `trainer_details` (
  `id_trainer` int(11) NOT NULL,
  `start_date` date NOT NULL,
  `phone_number` varchar(20) NOT NULL,
  `work_area` varchar(255) DEFAULT NULL,
  `bio` text DEFAULT NULL,
  `profile_picture` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_polish_ci;

--
-- Zrzut danych tabeli `trainer_details`
--

INSERT INTO `trainer_details` (`id_trainer`, `start_date`, `phone_number`, `work_area`, `bio`, `profile_picture`) VALUES
(14, '1111-03-21', '123123123', 'awdawd', NULL, NULL),
(15, '2025-02-10', '999888776', 'Rybnikk', 'Testy', 'a44fd281-cfa9-4aa1-9da0-04f4071b793f_DALL·E 2025-02-08 01.45.23 - A modern and minimalistic favicon for a fitness web application named \'GoGym\'. The design should be simple, clean, and recognizable at small sizes. It.webp'),
(16, '0222-02-22', '2312312', '', NULL, NULL);

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `trainer_experience`
--

CREATE TABLE `trainer_experience` (
  `id` int(11) NOT NULL,
  `id_trainer` int(11) NOT NULL,
  `graduation_name` varchar(255) NOT NULL,
  `graduation_date` date DEFAULT NULL,
  `certification_file` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_polish_ci;

--
-- Zrzut danych tabeli `trainer_experience`
--

INSERT INTO `trainer_experience` (`id`, `id_trainer`, `graduation_name`, `graduation_date`, `certification_file`) VALUES
(3, 15, 'TESTY', '2025-02-08', '304b2d2d-b7a1-4eb5-ac2b-8aefd3b77f39_image_white_background.png'),
(9, 15, 'DCDC', '2025-02-10', '51a59867-dabd-48ff-960c-4c803b0fe04a_DALL·E 2025-02-08 01.45.23 - A modern and minimalistic favicon for a fitness web application named \'GoGym\'. The design should be simple, clean, and recognizable at small sizes. It.webp');

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `trainer_specialization`
--

CREATE TABLE `trainer_specialization` (
  `id` int(11) NOT NULL,
  `id_trainer` int(11) NOT NULL,
  `specialization` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_polish_ci;

--
-- Zrzut danych tabeli `trainer_specialization`
--

INSERT INTO `trainer_specialization` (`id`, `id_trainer`, `specialization`) VALUES
(3, 14, 'wdawd'),
(7, 16, '1312312'),
(9, 15, 'Specjalizacja dla Ta'),
(20, 15, 'A');

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `training_plans`
--

CREATE TABLE `training_plans` (
  `id_plan` int(11) NOT NULL,
  `name` varchar(50) NOT NULL,
  `start_date` date NOT NULL,
  `end_date` date DEFAULT NULL,
  `description` text NOT NULL,
  `status` enum('active','completed') DEFAULT NULL,
  `id_trainer` int(11) NOT NULL,
  `id_client` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_polish_ci;

--
-- Zrzut danych tabeli `training_plans`
--

INSERT INTO `training_plans` (`id_plan`, `name`, `start_date`, `end_date`, `description`, `status`, `id_trainer`, `id_client`) VALUES
(13, 'Plan2', '2025-01-22', '2025-01-23', 'Plan2', 'completed', 3, 5),
(15, 'Plan4', '2025-02-21', '2025-02-23', 'Plan4', 'active', 3, 5),
(16, 'Plan5', '2025-01-23', '2025-01-24', 'Plan5', 'active', 3, 5),
(17, 'Plan6', '2025-03-19', '2025-03-21', 'Plan6', 'active', 3, 5),
(18, 'Plan7', '2025-01-22', '2025-01-24', 'Plan7', 'active', 3, 5),
(19, 'Plan8', '2025-01-24', '2025-01-25', 'Plan8', 'completed', 3, 5),
(20, 'Plan9', '2025-01-22', '2025-01-24', 'Plan9', 'completed', 3, 5),
(21, 'plan10', '2025-01-22', '2025-01-24', 'plan10', 'completed', 3, 5),
(23, 'Plan12', '2025-01-22', '2025-01-23', 'Plan12', 'completed', 3, 5),
(24, 'Plan13', '2025-01-29', '2025-01-31', 'Plan13', 'active', 3, 5),
(25, 'PlanDlaA', '2025-01-30', '2025-02-01', 'A', 'active', 3, 2),
(26, 'A2zmiana', '2025-02-05', '2025-02-06', 'A2', 'active', 3, 2);

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `training_plan_days`
--

CREATE TABLE `training_plan_days` (
  `id_day` int(11) NOT NULL,
  `day_type` enum('training','rest') NOT NULL,
  `status` enum('notCompleted','completed') NOT NULL DEFAULT 'notCompleted',
  `notes` text DEFAULT NULL,
  `date` date NOT NULL,
  `id_plan` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_polish_ci;

--
-- Zrzut danych tabeli `training_plan_days`
--

INSERT INTO `training_plan_days` (`id_day`, `day_type`, `status`, `notes`, `date`, `id_plan`) VALUES
(20, 'training', 'completed', 'dzien1', '2025-01-22', 13),
(21, 'training', 'completed', 'dzien2', '2025-01-23', 13),
(26, 'training', 'notCompleted', 'dzien1', '2025-02-21', 15),
(27, 'rest', 'notCompleted', 'dzien2', '2025-02-22', 15),
(28, 'training', 'notCompleted', 'dzien3', '2025-02-23', 15),
(29, 'training', 'notCompleted', 'DZIEN1', '2025-01-23', 16),
(30, 'training', 'notCompleted', 'DZIEN2', '2025-01-24', 16),
(31, 'training', 'notCompleted', 'dzien1', '2025-03-19', 17),
(32, 'rest', 'notCompleted', 'dzien2', '2025-03-20', 17),
(33, 'training', 'notCompleted', 'dzien3', '2025-03-21', 17),
(34, 'training', 'notCompleted', 'dzien1', '2025-01-22', 18),
(35, 'training', 'notCompleted', 'dzien2', '2025-01-23', 18),
(36, 'training', 'notCompleted', 'dzien3', '2025-01-24', 18),
(37, 'training', 'completed', 'dzien1', '2025-01-24', 19),
(38, 'training', 'completed', 'dzien2', '2025-01-25', 19),
(39, 'training', 'completed', '1', '2025-01-22', 20),
(40, 'training', 'completed', '2', '2025-01-23', 20),
(41, 'training', 'completed', '3', '2025-01-24', 20),
(42, 'training', 'completed', 'd1', '2025-01-22', 21),
(43, 'training', 'completed', 'd2', '2025-01-23', 21),
(44, 'training', 'completed', 'd3', '2025-01-24', 21),
(46, 'training', 'completed', 'dzien1', '2025-01-22', 23),
(47, 'training', 'completed', 'dzien2', '2025-01-23', 23),
(48, 'training', 'completed', 'd1', '2025-01-29', 24),
(49, 'rest', 'completed', 'd2', '2025-01-30', 24),
(50, 'training', 'notCompleted', 'd3', '2025-01-31', 24),
(51, 'training', 'notCompleted', 'Dzień1A', '2025-01-30', 25),
(52, 'rest', 'notCompleted', 'Dzień2A', '2025-01-31', 25),
(53, 'training', 'notCompleted', 'Dzień3A', '2025-02-01', 25),
(54, 'training', 'notCompleted', 'A2', '2025-02-05', 26),
(55, 'training', 'notCompleted', 'A22', '2025-02-06', 26),
(72, 'rest', 'completed', '', '2025-01-31', 13),
(73, 'training', 'completed', '', '2025-01-31', 13);

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `treiners_clients`
--

CREATE TABLE `treiners_clients` (
  `id` int(11) NOT NULL,
  `id_trainer` int(11) NOT NULL,
  `id_client` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_polish_ci;

--
-- Zrzut danych tabeli `treiners_clients`
--

INSERT INTO `treiners_clients` (`id`, `id_trainer`, `id_client`) VALUES
(12, 3, 2),
(11, 3, 5),
(73, 4, 5);

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `user`
--

CREATE TABLE `user` (
  `id_user` int(11) NOT NULL,
  `email` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `first_name` varchar(30) NOT NULL,
  `second_name` varchar(30) NOT NULL,
  `birth_date` date NOT NULL,
  `gender` enum('KOBIETA','MĘŻCZYZNA') NOT NULL,
  `user_type` enum('UŻYTKOWNIK','TRENER') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_polish_ci;

--
-- Zrzut danych tabeli `user`
--

INSERT INTO `user` (`id_user`, `email`, `password`, `first_name`, `second_name`, `birth_date`, `gender`, `user_type`) VALUES
(2, 'A', '$2a$10$fk5ZhEsdK0eCJglnxznvVeVoxrZCfIYgVs3v7.oOQo4muXGowXvjm', 'Aa', 'Aa', '2025-01-02', 'KOBIETA', 'UŻYTKOWNIK'),
(3, 'jan@jan.pl', '$2a$10$1YRXlh0SPlA5pUMmiEhYGOfaTTvvc3RyFF8HPZuV/MYEg6pU5QD5S', 'Jan', 'Dziwok', '2001-10-02', 'MĘŻCZYZNA', 'TRENER'),
(4, 'b', '$2a$10$eRJbNm5HNpB6IWhHp6sKD.UOMcUK3Va.dmNyzkDLUnR5AA73TVIeS', 'Bb', 'Bb', '2025-01-06', 'KOBIETA', 'TRENER'),
(5, 'test@test.pl', '$2a$10$fOdBhiVmycq6t9yPjtey5uYQAY/5PHJaak7JQlXNCYdcEm3L7wckO', 'Test', 'Testowy', '2024-12-30', 'MĘŻCZYZNA', 'UŻYTKOWNIK'),
(10, 'trener', '$2a$10$gSw2bQ/ysuiaPIMTMM7Ilu63Fe0/.v3j7fh1JtORIpYSGYjFJiwO.', 'Leo', 'Messi', '2001-10-01', 'MĘŻCZYZNA', 'TRENER'),
(11, 'U', '$2a$10$uVhzTjmOjTRmgbs6bRC8I.P/09Apd/W5bG97cdhHogX02kyZ2V0cm', 'Uu', 'Uu', '2002-01-01', 'KOBIETA', 'UŻYTKOWNIK'),
(13, 'm', '$2a$10$bb5soK1gUAwfuIj/MDk6rOorR1SgtL4ryBrPdNmnuip01DeIbYi/.', 'Mm', 'Mm', '2010-01-01', 'KOBIETA', 'TRENER'),
(14, 'adw', '$2a$10$4/7kNbpU0tAH0HS4Yatnquya9e99To.G53O9cy/.3PcFRErTXm5QK', 'Awdawd', 'Adwaadw', '1222-12-12', 'KOBIETA', 'TRENER'),
(15, 't', '$2a$10$AmVuDAMIdnMgEMv7vBBcieb/fv4ouBec97n7YP5XjDubPLW0XyE7C', 'Tt', 'Tt', '2222-02-22', 'KOBIETA', 'TRENER'),
(16, 'aaa', '$2a$10$z8ToxpG8XLFh0CSpWhScuOk77nF8cMdMQEPqsjnQWM1kcGpW11j7i', 'Aaaa', 'Aaaa', '0222-02-22', 'KOBIETA', 'TRENER');

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `workouts`
--

CREATE TABLE `workouts` (
  `id_workout` int(11) NOT NULL,
  `workout_date` date DEFAULT NULL,
  `intensity` enum('low','medium','high') NOT NULL,
  `notes` text DEFAULT NULL,
  `start_time` time DEFAULT NULL,
  `end_time` time DEFAULT NULL,
  `id_user` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_polish_ci;

--
-- Zrzut danych tabeli `workouts`
--

INSERT INTO `workouts` (`id_workout`, `workout_date`, `intensity`, `notes`, `start_time`, `end_time`, `id_user`) VALUES
(13, '2025-02-10', 'low', 'Tren', '05:15:00', '06:15:00', 5);

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `workout_exercises`
--

CREATE TABLE `workout_exercises` (
  `id` int(11) NOT NULL,
  `sets` int(11) NOT NULL,
  `reps` int(11) NOT NULL,
  `weight` double NOT NULL,
  `id_workout` int(11) NOT NULL,
  `id_exercise` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_polish_ci;

--
-- Zrzut danych tabeli `workout_exercises`
--

INSERT INTO `workout_exercises` (`id`, `sets`, `reps`, `weight`, `id_workout`, `id_exercise`) VALUES
(9, 2, 2, 0.5, 13, 18);

--
-- Indeksy dla zrzutów tabel
--

--
-- Indeksy dla tabeli `body_part`
--
ALTER TABLE `body_part`
  ADD PRIMARY KEY (`id_bodypart`);

--
-- Indeksy dla tabeli `client_details`
--
ALTER TABLE `client_details`
  ADD PRIMARY KEY (`id`),
  ADD KEY `id_user` (`id_user`);

--
-- Indeksy dla tabeli `equipment`
--
ALTER TABLE `equipment`
  ADD PRIMARY KEY (`id_equipment`);

--
-- Indeksy dla tabeli `exercise`
--
ALTER TABLE `exercise`
  ADD PRIMARY KEY (`id_exercise`);

--
-- Indeksy dla tabeli `exercise_bodypart`
--
ALTER TABLE `exercise_bodypart`
  ADD PRIMARY KEY (`id_exercise`,`id_bodypart`),
  ADD KEY `id_bodypart` (`id_bodypart`);

--
-- Indeksy dla tabeli `exercise_equipment`
--
ALTER TABLE `exercise_equipment`
  ADD PRIMARY KEY (`id_exercise`,`id_equipment`),
  ADD KEY `id_equipment` (`id_equipment`);

--
-- Indeksy dla tabeli `notifications`
--
ALTER TABLE `notifications`
  ADD PRIMARY KEY (`id_notification`),
  ADD KEY `user_id` (`user_id`);

--
-- Indeksy dla tabeli `plan_exercises`
--
ALTER TABLE `plan_exercises`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_plan_exercises_plan` (`id_plan`),
  ADD KEY `fk_plan_exercises_exercise` (`id_exercise`),
  ADD KEY `fk_day` (`id_day`);

--
-- Indeksy dla tabeli `requests`
--
ALTER TABLE `requests`
  ADD PRIMARY KEY (`id_request`),
  ADD KEY `id_trainer` (`id_trainer`),
  ADD KEY `id_client` (`id_client`);

--
-- Indeksy dla tabeli `trainer_details`
--
ALTER TABLE `trainer_details`
  ADD PRIMARY KEY (`id_trainer`);

--
-- Indeksy dla tabeli `trainer_experience`
--
ALTER TABLE `trainer_experience`
  ADD PRIMARY KEY (`id`),
  ADD KEY `id_trainer` (`id_trainer`);

--
-- Indeksy dla tabeli `trainer_specialization`
--
ALTER TABLE `trainer_specialization`
  ADD PRIMARY KEY (`id`),
  ADD KEY `id_trainer` (`id_trainer`);

--
-- Indeksy dla tabeli `training_plans`
--
ALTER TABLE `training_plans`
  ADD PRIMARY KEY (`id_plan`),
  ADD KEY `fk_training_plans_trainer` (`id_trainer`),
  ADD KEY `fk_training_plans_client` (`id_client`);

--
-- Indeksy dla tabeli `training_plan_days`
--
ALTER TABLE `training_plan_days`
  ADD PRIMARY KEY (`id_day`),
  ADD KEY `id_plan` (`id_plan`);

--
-- Indeksy dla tabeli `treiners_clients`
--
ALTER TABLE `treiners_clients`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `unique_trainer_client` (`id_trainer`,`id_client`),
  ADD KEY `fk_trainers_clients_client` (`id_client`);

--
-- Indeksy dla tabeli `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`id_user`);

--
-- Indeksy dla tabeli `workouts`
--
ALTER TABLE `workouts`
  ADD PRIMARY KEY (`id_workout`),
  ADD KEY `fk_workouts_user` (`id_user`);

--
-- Indeksy dla tabeli `workout_exercises`
--
ALTER TABLE `workout_exercises`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_workout_exercises_workout` (`id_workout`),
  ADD KEY `fk_workout_exercises_exercise` (`id_exercise`);

--
-- AUTO_INCREMENT dla zrzuconych tabel
--

--
-- AUTO_INCREMENT dla tabeli `body_part`
--
ALTER TABLE `body_part`
  MODIFY `id_bodypart` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=15;

--
-- AUTO_INCREMENT dla tabeli `client_details`
--
ALTER TABLE `client_details`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT dla tabeli `equipment`
--
ALTER TABLE `equipment`
  MODIFY `id_equipment` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=20;

--
-- AUTO_INCREMENT dla tabeli `exercise`
--
ALTER TABLE `exercise`
  MODIFY `id_exercise` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=91;

--
-- AUTO_INCREMENT dla tabeli `notifications`
--
ALTER TABLE `notifications`
  MODIFY `id_notification` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT dla tabeli `plan_exercises`
--
ALTER TABLE `plan_exercises`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=80;

--
-- AUTO_INCREMENT dla tabeli `requests`
--
ALTER TABLE `requests`
  MODIFY `id_request` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=208;

--
-- AUTO_INCREMENT dla tabeli `trainer_experience`
--
ALTER TABLE `trainer_experience`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT dla tabeli `trainer_specialization`
--
ALTER TABLE `trainer_specialization`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=23;

--
-- AUTO_INCREMENT dla tabeli `training_plans`
--
ALTER TABLE `training_plans`
  MODIFY `id_plan` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=29;

--
-- AUTO_INCREMENT dla tabeli `training_plan_days`
--
ALTER TABLE `training_plan_days`
  MODIFY `id_day` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=74;

--
-- AUTO_INCREMENT dla tabeli `treiners_clients`
--
ALTER TABLE `treiners_clients`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=74;

--
-- AUTO_INCREMENT dla tabeli `user`
--
ALTER TABLE `user`
  MODIFY `id_user` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=17;

--
-- AUTO_INCREMENT dla tabeli `workouts`
--
ALTER TABLE `workouts`
  MODIFY `id_workout` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=14;

--
-- AUTO_INCREMENT dla tabeli `workout_exercises`
--
ALTER TABLE `workout_exercises`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- Ograniczenia dla zrzutów tabel
--

--
-- Ograniczenia dla tabeli `client_details`
--
ALTER TABLE `client_details`
  ADD CONSTRAINT `client_details_ibfk_1` FOREIGN KEY (`id_user`) REFERENCES `user` (`id_user`) ON DELETE CASCADE;

--
-- Ograniczenia dla tabeli `exercise_bodypart`
--
ALTER TABLE `exercise_bodypart`
  ADD CONSTRAINT `exercise_bodypart_ibfk_1` FOREIGN KEY (`id_exercise`) REFERENCES `exercise` (`id_exercise`) ON DELETE CASCADE,
  ADD CONSTRAINT `exercise_bodypart_ibfk_2` FOREIGN KEY (`id_bodypart`) REFERENCES `body_part` (`id_bodypart`) ON DELETE CASCADE;

--
-- Ograniczenia dla tabeli `exercise_equipment`
--
ALTER TABLE `exercise_equipment`
  ADD CONSTRAINT `exercise_equipment_ibfk_1` FOREIGN KEY (`id_exercise`) REFERENCES `exercise` (`id_exercise`) ON DELETE CASCADE,
  ADD CONSTRAINT `exercise_equipment_ibfk_2` FOREIGN KEY (`id_equipment`) REFERENCES `equipment` (`id_equipment`) ON DELETE CASCADE;

--
-- Ograniczenia dla tabeli `notifications`
--
ALTER TABLE `notifications`
  ADD CONSTRAINT `notifications_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id_user`) ON DELETE CASCADE;

--
-- Ograniczenia dla tabeli `plan_exercises`
--
ALTER TABLE `plan_exercises`
  ADD CONSTRAINT `fk_day` FOREIGN KEY (`id_day`) REFERENCES `training_plan_days` (`id_day`) ON DELETE CASCADE,
  ADD CONSTRAINT `fk_plan_exercises_exercise` FOREIGN KEY (`id_exercise`) REFERENCES `exercise` (`id_exercise`) ON DELETE CASCADE,
  ADD CONSTRAINT `fk_plan_exercises_plan` FOREIGN KEY (`id_plan`) REFERENCES `training_plans` (`id_plan`) ON DELETE CASCADE;

--
-- Ograniczenia dla tabeli `requests`
--
ALTER TABLE `requests`
  ADD CONSTRAINT `requests_ibfk_1` FOREIGN KEY (`id_trainer`) REFERENCES `user` (`id_user`) ON DELETE CASCADE,
  ADD CONSTRAINT `requests_ibfk_2` FOREIGN KEY (`id_client`) REFERENCES `user` (`id_user`) ON DELETE CASCADE;

--
-- Ograniczenia dla tabeli `trainer_details`
--
ALTER TABLE `trainer_details`
  ADD CONSTRAINT `trainer_details_ibfk_1` FOREIGN KEY (`id_trainer`) REFERENCES `user` (`id_user`) ON DELETE CASCADE;

--
-- Ograniczenia dla tabeli `trainer_experience`
--
ALTER TABLE `trainer_experience`
  ADD CONSTRAINT `trainer_experience_ibfk_1` FOREIGN KEY (`id_trainer`) REFERENCES `user` (`id_user`) ON DELETE CASCADE;

--
-- Ograniczenia dla tabeli `trainer_specialization`
--
ALTER TABLE `trainer_specialization`
  ADD CONSTRAINT `trainer_specialization_ibfk_1` FOREIGN KEY (`id_trainer`) REFERENCES `trainer_details` (`id_trainer`) ON DELETE CASCADE;

--
-- Ograniczenia dla tabeli `training_plans`
--
ALTER TABLE `training_plans`
  ADD CONSTRAINT `fk_training_plans_client` FOREIGN KEY (`id_client`) REFERENCES `user` (`id_user`) ON DELETE CASCADE,
  ADD CONSTRAINT `fk_training_plans_trainer` FOREIGN KEY (`id_trainer`) REFERENCES `user` (`id_user`) ON DELETE CASCADE;

--
-- Ograniczenia dla tabeli `training_plan_days`
--
ALTER TABLE `training_plan_days`
  ADD CONSTRAINT `training_plan_days_ibfk_1` FOREIGN KEY (`id_plan`) REFERENCES `training_plans` (`id_plan`) ON DELETE CASCADE;

--
-- Ograniczenia dla tabeli `treiners_clients`
--
ALTER TABLE `treiners_clients`
  ADD CONSTRAINT `fk_trainers_clients_client` FOREIGN KEY (`id_client`) REFERENCES `user` (`id_user`) ON DELETE CASCADE,
  ADD CONSTRAINT `fk_trainers_clients_trainer` FOREIGN KEY (`id_trainer`) REFERENCES `user` (`id_user`) ON DELETE CASCADE;

--
-- Ograniczenia dla tabeli `workouts`
--
ALTER TABLE `workouts`
  ADD CONSTRAINT `fk_workouts_user` FOREIGN KEY (`id_user`) REFERENCES `user` (`id_user`) ON DELETE CASCADE;

--
-- Ograniczenia dla tabeli `workout_exercises`
--
ALTER TABLE `workout_exercises`
  ADD CONSTRAINT `fk_workout_exercises_exercise` FOREIGN KEY (`id_exercise`) REFERENCES `exercise` (`id_exercise`) ON DELETE CASCADE,
  ADD CONSTRAINT `fk_workout_exercises_workout` FOREIGN KEY (`id_workout`) REFERENCES `workouts` (`id_workout`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
