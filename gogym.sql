-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Maj 19, 2025 at 12:51 AM
-- Wersja serwera: 10.4.32-MariaDB
-- Wersja PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `gogym`
--

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `badge`
--

CREATE TABLE `badge` (
  `id` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  `description` text DEFAULT NULL,
  `image_url` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_polish_ci;

--
-- Dumping data for table `badge`
--

INSERT INTO `badge` (`id`, `name`, `description`, `image_url`) VALUES
(1, 'Testowa1', 'Pierwsza testowa odznaka', '/images/pushup.jpg'),
(2, '5 Workouts Completed', 'Pięć stworzonych treningów', '/images/pushup.jpg'),
(3, '5 Plans Completed', 'Pięć ukończonych planów treningowych', '/images/pushup.jpg'),
(4, '10 Plan Exercises Completed', 'Dziesięć ukończonych ćwiczeń planowych', '/images/pushup.jpg'),
(5, '8 Plans Completed', 'Osiem ukończonych planów treningowych', '/images/pushup.jpg'),
(6, 'Testowa2', 'Druga testowa odznaka', '/images/pushup.jpg'),
(7, 'Testowa3', 'Trzecia testowa odznaka', '/images/pushup.jpg'),
(8, 'Testowa4', 'Czwarta testowa odznaka', '/images/pushup.jpg'),
(9, 'Testowa5', 'Piąta testowa odznaka', '/images/pushup.jpg');

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `body_part`
--

CREATE TABLE `body_part` (
  `id_bodypart` int(11) NOT NULL,
  `name` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_polish_ci;

--
-- Dumping data for table `body_part`
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
-- Struktura tabeli dla tabeli `chat_rooms`
--

CREATE TABLE `chat_rooms` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `trainer_id` int(11) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_polish_ci;

--
-- Dumping data for table `chat_rooms`
--

INSERT INTO `chat_rooms` (`id`, `user_id`, `trainer_id`, `created_at`) VALUES
(1, 23, 1, '2025-03-02 00:45:46'),
(2, 24, 1, '2025-03-02 00:47:46'),
(3, 23, 24, '2025-03-02 00:48:08'),
(5, 26, 1, '2025-03-24 18:59:45'),
(6, 27, 1, '2025-03-24 19:09:57'),
(7, 28, 1, '2025-03-24 19:12:36'),
(8, 29, 1, '2025-03-24 19:13:45'),
(10, 26, 27, '2025-03-24 19:31:06');

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
  `phone_number` varchar(20) DEFAULT NULL,
  `city` varchar(50) DEFAULT NULL,
  `update_date` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_polish_ci;

--
-- Dumping data for table `client_details`
--

INSERT INTO `client_details` (`id`, `id_user`, `weight`, `height`, `waist`, `hips`, `chest`, `profile_picture`, `phone_number`, `city`, `update_date`) VALUES
(1, 23, 90, 191, 70, 80, 110, 'e4f9433a-53cb-4340-a6ab-5014277341c5_dumbbell-1966247_1920.jpg', '+48123456789', 'Wodzisław Śląski', '2025-03-02');

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `equipment`
--

CREATE TABLE `equipment` (
  `id_equipment` int(11) NOT NULL,
  `name` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_polish_ci;

--
-- Dumping data for table `equipment`
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
  `type` enum('STRENGTH','CARDIO') NOT NULL,
  `jpg` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_polish_ci;

--
-- Dumping data for table `exercise`
--

INSERT INTO `exercise` (`id_exercise`, `name`, `description`, `difficulty`, `type`, `jpg`) VALUES
(1, 'Push-Up', 'Klasyczne pompki angażujące mięśnie klatki piersiowej, ramion i core.', 'beginner', 'STRENGTH', '/images/exercises/push-up.jpg'),
(2, 'Bodyweight Squat', 'Przysiady z wykorzystaniem masy własnego ciała, wzmacniające mięśnie nóg i pośladków.', 'beginner', 'STRENGTH', '/images/exercises/bodyweight_squat.jpg'),
(3, 'Plank', 'Izometryczne ćwiczenie wzmacniające mięśnie core, pleców i ramion.', 'beginner', 'CARDIO', '/images/exercises/plank.jpg'),
(4, 'Dumbbell Curl', 'Uginanie przedramion z hantlami, ukierunkowane na bicepsy.', 'beginner', 'STRENGTH', '/images/exercises/dumbbell_curl.jpg'),
(5, 'Lat Pulldown', 'Ćwiczenie na maszynie angażujące mięśnie pleców poprzez przyciąganie drążka do klatki piersiowej.', 'beginner', 'STRENGTH', '/images/exercises/lat_pulldown.jpg'),
(6, 'Leg Press', 'Przysiady na maszynie do wyciskania nogami, idealne na mięśnie nóg.', 'beginner', 'STRENGTH', '/images/exercises/leg_press.jpg'),
(7, 'Seated Row', 'Wiosłowanie na maszynie, wzmacniające plecy i ramiona.', 'beginner', 'STRENGTH', '/images/exercises/seated_row.jpg'),
(8, 'Incline Walk', 'Marsz na bieżni pod kątem, idealny dla poprawy kondycji i spalania kalorii.', 'beginner', 'CARDIO', '/images/exercises/incline_walk.jpg'),
(9, 'Kettlebell Deadlift', 'Martwy ciąg z odważnikiem kettlebell, wzmacniający mięśnie nóg, pośladków i pleców.', 'beginner', 'STRENGTH', '/images/exercises/kettlebell_deadlift.jpg'),
(10, 'Chest Press (Machine)', 'Wyciskanie na maszynie wzmacniające mięśnie klatki piersiowej i ramion.', 'beginner', 'STRENGTH', '/images/exercises/chest_press_(machine).jpg'),
(11, 'Leg Curl (Machine)', 'Uginanie nóg na maszynie, wzmacniające mięśnie dwugłowe uda.', 'beginner', 'STRENGTH', '/images/exercises/leg_curl_(machine).jpg'),
(12, 'Arm Curl (Machine)', 'Arm Curl (Machine) to izolowane ćwiczenie siłowe skoncentrowane na wzmacnianiu mięśni dwugłowych ramion (biceps brachii). Dzięki zastosowaniu maszyny ruch jest kontrolowany, co minimalizuje ryzyko błędów technicznych oraz pomaga w pełnym skupieniu na pracy mięśni. Ćwiczenie jest odpowiednie zarówno dla początkujących, jak i zaawansowanych sportowców, pozwalając na progresję siłową oraz poprawę wytrzymałości mięśni ramion.\r\n\r\nInstrukcja wykonania:\r\n1. Dostosuj wysokość siedziska tak, aby łokcie znajdowały się na poziomie podparcia ramion.\r\n2. Chwyć uchwyty maszyny podchwytem (dłonie skierowane do góry), upewniając się, że nadgarstki są w linii przedramion.\r\n3. Ustaw odpowiednie obciążenie, dostosowane do Twojego poziomu siły.\r\n4. Weź wdech i powoli zacznij uginać ramiona w łokciach, przyciągając uchwyty w kierunku barków.\r\n5. Utrzymuj łokcie nieruchomo – cały ruch powinien odbywać się jedynie w stawach łokciowych.\r\n6. Gdy osiągniesz maksymalne napięcie bicepsów (uchwyty blisko barków), zatrzymaj ruch na sekundę, napinając mięśnie.\r\n7. Powoli opuszczaj uchwyty do pozycji wyjściowej, kontrolując ruch i nie dopuszczając do gwałtownego opadania ciężaru.\r\n\r\nOddychanie:\r\n-Wdech wykonuj w trakcie opuszczania uchwytów.\r\n-Wydech wykonuj podczas unoszenia ciężaru.\r\n\r\nWskazówki dotyczące techniki:\r\n-Nie używaj nadmiernego obciążenia, które zmusi Cię do angażowania innych grup mięśniowych (np. kołysania tułowiem).\r\n-Ruch powinien być płynny i kontrolowany, bez nagłych szarpnięć.\r\n-Upewnij się, że stopy stabilnie przylegają do podłoża.', 'beginner', 'STRENGTH', '/images/exercises/arm_curl_(machine).jpg'),
(13, 'Shoulder Press (Machine)', 'Wyciskanie na maszynie wzmacniające mięśnie naramienne.', 'beginner', 'STRENGTH', '/images/exercises/shoulder_press_(machine).jpg'),
(14, 'Tricep Dips (Bench)', 'Ćwiczenie na tricepsy wykonywane z pomocą ławki.', 'beginner', 'STRENGTH', '/images/exercises/tricep_dips_(bench).jpg'),
(15, 'Incline Dumbbell Fly', 'Rozpiętki na ławce skośnej, wzmacniające klatkę piersiową.', 'beginner', 'STRENGTH', '/images/exercises/incline_dumbbell_fly.jpg'),
(16, 'Step-Ups', 'Wejścia na podwyższenie, angażujące nogi i pośladki.', 'beginner', 'CARDIO', '/images/exercises/step-ups.jpg'),
(17, 'Wall Sit', 'Izometryczne ćwiczenie nóg polegające na „siedzeniu” przy ścianie.', 'beginner', 'CARDIO', '/images/exercises/wall_sit.jpg'),
(18, 'Side Plank', 'Plank boczny wzmacniający mięśnie skośne brzucha i core.', 'beginner', 'CARDIO', '/images/exercises/side_plank.jpg'),
(19, 'Cable Chest Fly', 'Rozpiętki na wyciągu, angażujące klatkę piersiową.', 'beginner', 'STRENGTH', '/images/exercises/cable_chest_fly.jpg'),
(20, 'Treadmill Jogging', 'Lekki bieg na bieżni, poprawiający kondycję i spalanie kalorii.', 'beginner', 'CARDIO', '/images/exercises/treadmill_jogging.jpg'),
(21, 'Pull-Up', 'Podciąganie na drążku, angażujące plecy, bicepsy i core.', 'intermediate', 'STRENGTH', '/images/exercises/pull-up.jpg'),
(22, 'Barbell Squat', 'Przysiady ze sztangą, kompleksowe ćwiczenie na nogi, pośladki i core.', 'intermediate', 'STRENGTH', '/images/exercises/barbell_squat.jpg'),
(23, 'Deadlift', 'Martwy ciąg ze sztangą, ćwiczenie na plecy, nogi i pośladki.', 'intermediate', 'STRENGTH', '/images/exercises/deadlift.jpg'),
(24, 'Dumbbell Bench Press', 'Wyciskanie hantli na ławce, angażujące klatkę piersiową i tricepsy.', 'intermediate', 'STRENGTH', '/images/exercises/dumbbell_bench_press.jpg'),
(25, 'Cable Tricep Pushdown', 'Prostowanie ramion na wyciągu, ćwiczenie na tricepsy.', 'intermediate', 'STRENGTH', '/images/exercises/cable_tricep_pushdown.jpg'),
(26, 'Overhead Shoulder Press (Machine)', 'Wyciskanie nad głowę na maszynie, wzmacniające barki.', 'intermediate', 'STRENGTH', '/images/exercises/overhead_shoulder_press_(machine).jpg'),
(27, 'Romanian Deadlift', 'Martwy ciąg na prostych nogach z hantlami lub sztangą, wzmacniający pośladki i mięśnie dwugłowe uda.', 'intermediate', 'STRENGTH', '/images/exercises/romanian_deadlift.jpg'),
(28, 'Lunges', 'Wykroki z hantlami, angażujące nogi i pośladki.', 'intermediate', 'STRENGTH', '/images/exercises/lunges.jpg'),
(29, 'Russian Twist', 'Skręty tułowia z obciążeniem, wzmacniające mięśnie skośne brzucha.', 'intermediate', 'STRENGTH', '/images/exercises/russian_twist.jpg'),
(30, 'Battle Ropes', 'Dynamiczne ruchy linami angażujące całe ciało i poprawiające kondycję.', 'intermediate', 'CARDIO', '/images/exercises/battle_ropes.jpg'),
(31, 'Goblet Squat', 'Przysiad z kettlebellem trzymanym przy klatce piersiowej.', 'intermediate', 'STRENGTH', '/images/exercises/goblet_squat.jpg'),
(32, 'One-Arm Dumbbell Row', 'Wiosłowanie jednorącz z hantlą, wzmacniające plecy i ramiona.', 'intermediate', 'STRENGTH', '/images/exercises/one-arm_dumbbell_row.jpg'),
(33, 'Incline Bench Press', 'Wyciskanie sztangi na ławce skośnej, ukierunkowane na górną część klatki piersiowej.', 'intermediate', 'STRENGTH', '/images/exercises/incline_bench_press.jpg'),
(34, 'Cable Lateral Raise', 'Unoszenie ramion na boki na wyciągu, wzmacniające barki.', 'intermediate', 'STRENGTH', '/images/exercises/cable_lateral_raise.jpg'),
(35, 'Roman Chair Back Extension', 'Prostowanie pleców na ławce rzymskiej, wzmacniające dolną część pleców.', 'intermediate', 'STRENGTH', '/images/exercises/roman_chair_back_extension.jpg'),
(36, 'Kettlebell Swing', 'Dynamiczne ćwiczenie angażujące całe ciało, szczególnie pośladki i plecy.', 'intermediate', 'STRENGTH', '/images/exercises/kettlebell_swing.jpg'),
(37, 'Walking Lunges', 'Chodzone wykroki z obciążeniem lub bez, angażujące nogi i pośladki.', 'intermediate', 'STRENGTH', '/images/exercises/walking_lunges.jpg'),
(38, 'Chest Dip', 'Pompki na poręczach, angażujące klatkę piersiową i tricepsy.', 'intermediate', 'STRENGTH', '/images/exercises/chest_dip.jpg'),
(39, 'Hammer Curl', 'Uginanie ramion z hantlami w neutralnym uchwycie, wzmacniające bicepsy i przedramiona.', 'intermediate', 'STRENGTH', '/images/exercises/hammer_curl.jpg'),
(40, 'Overhead Dumbbell Triceps Extension', 'Prostowanie ramion nad głową z hantlą, ćwiczenie na tricepsy.', 'intermediate', 'STRENGTH', '/images/exercises/overhead_dumbbell_triceps_extension.jpg'),
(41, 'Snatch', 'Rwanie sztangi nad głowę, ćwiczenie dynamiczne angażujące całe ciało.', 'advanced', 'STRENGTH', '/images/exercises/snatch.jpg'),
(42, 'Clean and Jerk', 'Podrzut sztangi nad głowę, ćwiczenie złożone na siłę i dynamikę.', 'advanced', 'STRENGTH', '/images/exercises/clean_and_jerk.jpg'),
(43, 'Pistol Squat', 'Przysiady na jednej nodze, wzmacniające nogi, pośladki i poprawiające równowagę.', 'advanced', 'STRENGTH', '/images/exercises/pistol_squat.jpg'),
(44, 'Muscle-Up', 'Podciąganie z przejściem nad drążek, angażujące całe górne partie ciała.', 'advanced', 'STRENGTH', '/images/exercises/muscle-up.jpg'),
(45, 'Bench Press', 'Wyciskanie sztangi na ławce poziomej, ćwiczenie na klatkę piersiową i ramiona.', 'advanced', 'STRENGTH', '/images/exercises/bench_press.jpg'),
(46, 'Bulgarian Split Squat', 'Przysiady na jednej nodze z tylną nogą na podwyższeniu, wzmacniające nogi i pośladki.', 'advanced', 'STRENGTH', '/images/exercises/bulgarian_split_squat.jpg'),
(47, 'Overhead Squat', 'Przysiad ze sztangą trzymaną nad głową, wymagający siły i stabilności.', 'advanced', 'STRENGTH', '/images/exercises/overhead_squat.jpg'),
(48, 'Front Squat', 'Przysiady ze sztangą z przodu, angażujące głównie nogi i core.', 'advanced', 'STRENGTH', '/images/exercises/front_squat.jpg'),
(49, 'Sled Push', 'Pchanie sanek z obciążeniem, dynamiczne ćwiczenie na nogi i pośladki.', 'advanced', 'STRENGTH', '/images/exercises/sled_push.jpg'),
(50, 'Rope Climb', 'Wspinanie się po linie, wymagające siły ramion, pleców i core.', 'advanced', 'STRENGTH', '/images/exercises/rope_climb.jpg'),
(51, 'Turkish Get-Up', 'Wstawanie z pozycji leżącej z kettlebellem, ćwiczenie na całe ciało i stabilność.', 'advanced', 'STRENGTH', '/images/exercises/turkish_get-up.jpg'),
(52, 'Farmers Walk', 'Spacer z ciężarami, angażujący całe ciało i poprawiający chwyt.', 'advanced', 'STRENGTH', '/images/exercises/farmers_walk.jpg'),
(53, 'Barbell Hip Thrust', 'Wypychanie bioder ze sztangą, ćwiczenie na pośladki.', 'advanced', 'STRENGTH', '/images/exercises/barbell_hip_thrust.jpg'),
(54, 'Barbell Overhead Press', 'Wyciskanie sztangi nad głowę, ćwiczenie na barki i core.', 'advanced', 'STRENGTH', '/images/exercises/barbell_overhead_press.jpg'),
(55, 'Good Morning', 'Skłony ze sztangą, wzmacniające plecy i mięśnie dwugłowe uda.', 'advanced', 'STRENGTH', '/images/exercises/good_morning.jpg'),
(56, 'Barbell Front Lunge', 'Wykroki z przodu z obciążeniem, wzmacniające nogi i pośladki.', 'advanced', 'STRENGTH', '/images/exercises/barbell_front_lunge.jpg'),
(57, 'Windshield Wipers', 'Ćwiczenie na mięśnie brzucha polegające na skrętach nóg w leżeniu.', 'advanced', 'STRENGTH', '/images/exercises/windshield_wipers.jpg'),
(58, 'Hanging Leg Raise', 'Podnoszenie nóg w zwisie na drążku, wzmacniające mięśnie brzucha i biodra.', 'advanced', 'STRENGTH', '/images/exercises/hanging_leg_raise.jpg'),
(59, 'Single-Leg Deadlift', 'Martwy ciąg na jednej nodze z hantlami lub sztangą, wymagający równowagi i siły.', 'advanced', 'STRENGTH', '/images/exercises/single-leg_deadlift.jpg'),
(60, 'Clean Pull', 'Pociągnięcie sztangi w ruchu przygotowującym do rwania, ćwiczenie na siłę i dynamikę.', 'advanced', 'STRENGTH', '/images/exercises/clean_pull.jpg'),
(61, 'Dumbbell Shoulder Press', 'Wyciskanie hantli nad głową, angażujące mięśnie naramienne i górną część klatki piersiowej.', 'beginner', 'STRENGTH', '/images/exercises/dumbbell_shoulder_press.jpg'),
(62, 'Standing Calf Raise (Machine)', 'Wzmacnianie mięśni łydek przy użyciu maszyny do wznosów.', 'beginner', 'STRENGTH', '/images/exercises/standing_calf_raise_(machine).jpg'),
(63, 'Leg Extension (Machine)', 'Rozciąganie nóg na maszynie, angażujące mięśnie czworogłowe uda.', 'beginner', 'STRENGTH', '/images/exercises/leg_extension_(machine).jpg'),
(64, 'Bent Over Dumbbell Fly', 'Unoszenie hantli w opadzie, wzmacniające tylne partie barków i pleców.', 'beginner', 'STRENGTH', '/images/exercises/bent_over_dumbbell_fly.jpg'),
(65, 'Incline Leg Press', 'Ćwiczenie nóg na maszynie do wyciskania, wzmacniające uda i pośladki.', 'beginner', 'STRENGTH', '/images/exercises/incline_leg_press.jpg'),
(66, 'Mountain Climbers', 'Ćwiczenie dynamiczne angażujące core, nogi i ramiona, przypominające wspinaczkę.', 'beginner', 'STRENGTH', '/images/exercises/mountain_climbers.jpg'),
(67, 'Bird-Dog', 'Ćwiczenie na równowagę i stabilność, angażujące mięśnie pleców i brzucha.', 'beginner', 'STRENGTH', '/images/exercises/bird-dog.jpg'),
(68, 'Glute Bridge', 'Wzmacnianie pośladków i dolnej części pleców, leżąc na plecach i unosząc biodra.', 'beginner', 'STRENGTH', '/images/exercises/glute_bridge.jpg'),
(69, 'Seated Row (Machine)', 'Wiosłowanie na maszynie, angażujące mięśnie pleców, ramion i core.', 'beginner', 'STRENGTH', '/images/exercises/seated_row_(machine).jpg'),
(70, 'Treadmill Walking', 'Marsz na bieżni, idealny dla poprawy kondycji i spalania tłuszczu.', 'beginner', 'STRENGTH', '/images/exercises/treadmill_walking.jpg'),
(71, 'Reverse Lunge', 'Wykrok wstecz, angażujący mięśnie nóg i pośladków, może być wykonywany z hantlami lub sztangą.', 'intermediate', 'STRENGTH', '/images/exercises/reverse_lunge.jpg'),
(72, 'Dumbbell Shoulder Raise', 'Unoszenie hantli na boki w celu wzmocnienia mięśni naramiennych.', 'intermediate', 'STRENGTH', '/images/exercises/dumbbell_shoulder_raise.jpg'),
(73, 'Chest Fly (Machine)', 'Rozpiętki na maszynie, angażujące głównie mięśnie klatki piersiowej.', 'intermediate', 'STRENGTH', '/images/exercises/chest_fly_(machine).jpg'),
(74, 'Cable Row', 'Wiosłowanie na wyciągu, angażujące plecy, ramiona i core.', 'intermediate', 'STRENGTH', '/images/exercises/cable_row.jpg'),
(75, 'Leg Press Calf Raise', 'Wzmacnianie mięśni łydek przy użyciu maszyny do wyciskania nóg.', 'intermediate', 'STRENGTH', '/images/exercises/leg_press_calf_raise.jpg'),
(76, 'Russian Twist with Weight', 'Skręty tułowia z obciążeniem, angażujące mięśnie brzucha i skośne.', 'intermediate', 'STRENGTH', '/images/exercises/russian_twist_with_weight.jpg'),
(77, 'Dumbbell Shrug', 'Unoszenie barków z hantlami, wzmacniające mięśnie czworoboczne.', 'intermediate', 'STRENGTH', '/images/exercises/dumbbell_shrug.jpg'),
(78, 'Machine Chest Press', 'Wyciskanie na maszynie, wzmacniające mięśnie klatki piersiowej i tricepsy.', 'intermediate', 'STRENGTH', '/images/exercises/machine_chest_press.jpg'),
(79, 'Concentration Curl', 'Izolowane uginanie ramienia z hantlem, ukierunkowane na bicepsy.', 'intermediate', 'STRENGTH', '/images/exercises/concentration_curl.jpg'),
(80, 'Box Jump', 'Wyskok na podwyższenie, doskonały na poprawę dynamiki i siły nóg.', 'intermediate', 'STRENGTH', '/images/exercises/box_jump.jpg'),
(81, 'Barbell Clean and Press', 'Podrzut i wyciskanie sztangi nad głowę, ćwiczenie na siłę i dynamikę.', 'advanced', 'STRENGTH', '/images/exercises/barbell_clean_and_press.jpg'),
(82, 'Plyometric Push-Up', 'Pompki z dynamicznym odbiciem, angażujące mięśnie klatki piersiowej, ramion i core.', 'advanced', 'STRENGTH', '/images/exercises/plyometric_push-up.jpg'),
(83, 'Barbell Bulgarian Split Squat', 'Przysiady na jednej nodze ze sztangą na plecach, angażujące nogi i pośladki.', 'advanced', 'STRENGTH', '/images/exercises/barbell_bulgarian_split_squat.jpg'),
(84, 'Barbell Bent Over Row', 'Wiosłowanie ze sztangą w opadzie, wzmacniające plecy i ramiona.', 'advanced', 'STRENGTH', '/images/exercises/barbell_bent_over_row.jpg'),
(85, 'Jump Squat', 'Przysiad z wyskokiem, angażujący mięśnie nóg, pośladków i poprawiający dynamikę.', 'advanced', 'STRENGTH', '/images/exercises/jump_squat.jpg'),
(86, 'Handstand Push-Up', 'Pompki przy ścianie w staniu na rękach, wymagające siły ramion i stabilności.', 'advanced', 'STRENGTH', '/images/exercises/handstand_push-up.jpg'),
(87, 'Dumbbell Renegade Row', 'Wiosłowanie z hantlami w pozycji pompki, angażujące plecy, ramiona i core.', 'advanced', 'STRENGTH', '/images/exercises/dumbbell_renegade_row.jpg'),
(88, 'Front Raise with Plate', 'Unoszenie talerza sztangi przed sobą, wzmacniające mięśnie barków.', 'advanced', 'STRENGTH', '/images/exercises/front_raise_with_plate.jpg'),
(89, 'Pistol Squat to Box', 'Przysiad na jednej nodze z pomocą podwyższenia, angażujący nogi, pośladki i równowagę.', 'advanced', 'STRENGTH', '/images/exercises/pistol_squat_to_box.jpg'),
(90, 'Tire Flip', 'Przewracanie opony, ćwiczenie siłowe angażujące całe ciało.', 'advanced', 'STRENGTH', '/images/exercises/tire_flip.jpg');

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `exercise_bodypart`
--

CREATE TABLE `exercise_bodypart` (
  `id_exercise` int(11) NOT NULL,
  `id_bodypart` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_polish_ci;

--
-- Dumping data for table `exercise_bodypart`
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
-- Dumping data for table `exercise_equipment`
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
-- Struktura tabeli dla tabeli `messages`
--

CREATE TABLE `messages` (
  `id` int(11) NOT NULL,
  `chat_room_id` int(11) NOT NULL,
  `sender_id` int(11) NOT NULL,
  `message` text NOT NULL,
  `sent_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `is_read` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_polish_ci;

--
-- Dumping data for table `messages`
--

INSERT INTO `messages` (`id`, `chat_room_id`, `sender_id`, `message`, `sent_at`, `is_read`) VALUES
(1, 3, 24, 'Hej Janek! Dzięki za zaufanie, liczę że wspólnie osiągniemy wiele!!!', '2025-03-02 00:52:44', 1),
(2, 3, 23, 'Witam trenerze! :D myślę że wspólnie poprawimy moją sylwetkę ', '2025-03-02 00:53:32', 1),
(4, 10, 26, 'Dzień dobry Pani Anno!', '2025-03-24 19:39:18', 1),
(5, 10, 27, 'Witam Panie Tomku! Nie mogę się doczekać rozpoczęcia w pełni naszej współpracy!!! :D', '2025-03-24 19:39:51', 0),
(6, 10, 26, 'Zależy mi na poprawie sylwetki, rozbudowaniu mięśni oraz delikatnym zmniejszeniu wagi. ', '2025-03-24 19:40:42', 0),
(7, 3, 23, 'Halo', '2025-03-31 16:32:35', 1);

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `notifications`
--

CREATE TABLE `notifications` (
  `id` int(11) NOT NULL,
  `id_user` int(11) NOT NULL,
  `message` varchar(255) NOT NULL,
  `status` enum('UNREAD','READ') NOT NULL DEFAULT 'UNREAD',
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_polish_ci;

--
-- Dumping data for table `notifications`
--

INSERT INTO `notifications` (`id`, `id_user`, `message`, `status`, `created_at`) VALUES
(1, 24, 'Nowa prośba o współpracę od Jan Kowalski.', 'UNREAD', '2025-03-02 00:47:56'),
(2, 23, 'Twoja prośba o współpracę z trenerem Piotr Nowak została zaakceptowana!', 'UNREAD', '2025-03-02 00:48:08'),
(3, 23, 'Twój trener Piotr Nowak stworzył dla Ciebie nowy plan: Plan treningowy nr1 dla Jana Kowalskiego.', 'UNREAD', '2025-03-02 00:51:58'),
(4, 24, 'Twój klient Jan Kowalski ukończył dzień treningowy: Dzień 1 w planie: Plan treningowy nr1 dla Jana Kowalskiego.', 'UNREAD', '2025-03-02 00:53:00'),
(5, 24, 'Twój klient Jan Kowalski ukończył dzień regeneracyjny: Dzień 2 (Regeneracja) w planie: Plan treningowy nr1 dla Jana Kowalskiego.', 'UNREAD', '2025-03-02 00:53:01'),
(6, 24, 'Twój klient Jan Kowalski ukończył plan treningowy: Plan treningowy nr1 dla Jana Kowalskiego.', 'UNREAD', '2025-03-02 00:53:04'),
(7, 24, 'Twój klient Jan Kowalski ukończył dzień treningowy: Dzień 3 w planie: Plan treningowy nr1 dla Jana Kowalskiego.', 'UNREAD', '2025-03-02 00:53:04'),
(8, 23, 'Twój trener Piotr Nowak edytował Twój plan: Plan treningowy nr1 dla Jana Kowalskiego.', 'UNREAD', '2025-03-02 01:01:31'),
(9, 23, 'Twój trener Piotr Nowak stworzył dla Ciebie nowy plan: Plan2.', 'UNREAD', '2025-03-02 01:02:30'),
(10, 23, 'Twój trener Piotr Nowak stworzył dla Ciebie nowy plan: awadw.', 'UNREAD', '2025-03-02 01:08:14'),
(11, 24, 'Twój klient Jan Kowalski ukończył plan treningowy: awadw.', 'UNREAD', '2025-03-02 01:10:38'),
(12, 24, 'Twój klient Jan Kowalski ukończył dzień treningowy: Dzień 1 w planie: awadw.', 'UNREAD', '2025-03-02 01:10:38'),
(13, 23, 'Twój trener Piotr Nowak stworzył dla Ciebie nowy plan: Plan 2.', 'UNREAD', '2025-03-10 19:21:53'),
(14, 24, 'Twój klient Jan Kowalski ukończył dzień treningowy: Dzień 2 w planie: Plan 2.', 'UNREAD', '2025-03-11 14:15:42'),
(15, 24, 'Twój klient Jan Kowalski ukończył dzień treningowy: Dzień 1 w planie: Plan 2.', 'UNREAD', '2025-03-11 14:15:43'),
(16, 24, 'Twój klient Jan Kowalski ukończył dzień regeneracyjny: Dzień 3 (Regeneracja) w planie: Plan 2.', 'READ', '2025-03-11 14:15:48'),
(17, 23, 'Twój trener Piotr Nowak stworzył dla Ciebie nowy plan: Aaa.', 'UNREAD', '2025-03-13 17:16:22'),
(18, 24, 'Twój klient Jan Kowalski ukończył dzień treningowy: Dzień 1 w planie: Aaa.', 'UNREAD', '2025-03-13 17:21:47'),
(19, 24, 'Twój klient Jan Kowalski ukończył dzień regeneracyjny: Dzień 2 (Regeneracja) w planie: Aaa.', 'UNREAD', '2025-03-13 17:21:52'),
(20, 24, 'Twój klient Jan Kowalski ukończył plan treningowy: Aaa.', 'UNREAD', '2025-03-13 17:21:52'),
(21, 27, 'Nowa prośba o współpracę od Tomasz Nowak.', 'UNREAD', '2025-03-24 19:22:53'),
(22, 27, 'Nowa prośba o współpracę od Tomasz Nowak.', 'UNREAD', '2025-03-24 19:23:04'),
(23, 27, 'Nowa prośba o współpracę od Tomasz Nowak.', 'UNREAD', '2025-03-24 19:23:09'),
(24, 27, 'Nowa prośba o współpracę od Tomasz Nowak.', 'UNREAD', '2025-03-24 19:23:25'),
(25, 28, 'Nowa prośba o współpracę od Tomasz Nowak.', 'UNREAD', '2025-03-24 19:23:30'),
(28, 27, 'Nowa prośba o współpracę od Tomasz Nowak.', 'UNREAD', '2025-03-24 19:29:04'),
(29, 26, 'Twoja prośba o współpracę z trenerem Anna Kowalska została zaakceptowana!', 'READ', '2025-03-24 19:31:06'),
(30, 26, 'Twój trener Anna Kowalska stworzył dla Ciebie nowy plan: Plan treningowy 1 - Tomasz Nowak.', 'UNREAD', '2025-03-24 22:31:41'),
(32, 27, 'Twój klient Tomasz Nowak ukończył dzień treningowy: Dzień 1 w planie: Plan treningowy 1 - Tomasz Nowak.', 'UNREAD', '2025-03-24 22:46:10'),
(33, 27, 'Twój klient Tomasz Nowak ukończył dzień treningowy: Dzień 1 w planie: Plan treningowy 1 - Tomasz Nowak.', 'UNREAD', '2025-03-24 22:49:51'),
(34, 28, 'Nowa prośba o współpracę od Jan Kowalski.', 'UNREAD', '2025-03-31 16:28:29'),
(35, 24, 'Twój klient Jan Kowalski ukończył dzień treningowy: Dzień 1 w planie: Aaa.', 'UNREAD', '2025-03-31 16:33:48'),
(36, 24, 'Twój klient Jan Kowalski ukończył dzień regeneracyjny: Dzień 2 (Regeneracja) w planie: Aaa.', 'UNREAD', '2025-03-31 16:33:58'),
(37, 24, 'Twój klient Jan Kowalski ukończył plan treningowy: Aaa.', 'UNREAD', '2025-03-31 16:33:58');

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `plan_exercises`
--

CREATE TABLE `plan_exercises` (
  `id` int(11) NOT NULL,
  `id_plan` int(11) NOT NULL,
  `id_exercise` int(11) NOT NULL,
  `sets` int(11) DEFAULT NULL,
  `reps` int(11) DEFAULT NULL,
  `weight` double DEFAULT NULL,
  `duration` int(11) DEFAULT NULL,
  `distance` double DEFAULT NULL,
  `id_day` int(11) NOT NULL,
  `status` enum('notCompleted','completed') DEFAULT 'notCompleted',
  `video_url` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_polish_ci;

--
-- Dumping data for table `plan_exercises`
--

INSERT INTO `plan_exercises` (`id`, `id_plan`, `id_exercise`, `sets`, `reps`, `weight`, `duration`, `distance`, `id_day`, `status`, `video_url`) VALUES
(1, 1, 1, 3, 10, NULL, NULL, NULL, 1, 'completed', NULL),
(2, 1, 10, 4, 8, 60, NULL, NULL, 1, 'completed', NULL),
(3, 1, 12, 3, 12, 45, NULL, NULL, 1, 'completed', NULL),
(4, 1, 2, 4, 20, NULL, NULL, NULL, 3, 'completed', NULL),
(5, 1, 6, 4, 10, 80, NULL, NULL, 3, 'completed', NULL),
(6, 1, 11, 3, 10, 55, NULL, NULL, 3, 'notCompleted', NULL),
(7, 1, 8, NULL, NULL, NULL, 1800, 5, 2, 'notCompleted', NULL),
(10, 4, 53, 4, 10, 80, NULL, NULL, 6, 'completed', NULL),
(11, 4, 61, 3, 12, 45, NULL, NULL, 7, 'completed', NULL),
(12, 4, 81, 2, 20, 35, NULL, NULL, 9, 'notCompleted', NULL),
(13, 5, 1, 4, 10, NULL, NULL, NULL, 10, 'completed', NULL),
(14, 5, 8, 2, NULL, NULL, 1800, 15, 10, 'completed', NULL),
(15, 6, 8, 2, NULL, NULL, 900, NULL, 12, 'completed', NULL),
(16, 6, 22, 4, 10, 70, NULL, NULL, 12, 'completed', 'https://res.cloudinary.com/drsbuhepf/video/upload/v1742856466/ry1gfkstikzxclihaki3.mp4'),
(17, 6, 1, 3, 20, NULL, NULL, NULL, 13, 'notCompleted', NULL),
(18, 6, 13, 4, 8, 60, NULL, NULL, 13, 'notCompleted', NULL),
(19, 6, 45, 3, 12, 50, NULL, NULL, 15, 'notCompleted', NULL),
(20, 6, 64, 4, 15, 35, NULL, NULL, 15, 'notCompleted', NULL),
(21, 6, 8, 3, NULL, NULL, 1200, NULL, 16, 'notCompleted', NULL);

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
-- Dumping data for table `requests`
--

INSERT INTO `requests` (`id_request`, `id_trainer`, `id_client`, `status`, `request_date`) VALUES
(8, 28, 23, 'pending', '2025-03-31 16:28:29');

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
-- Dumping data for table `trainer_details`
--

INSERT INTO `trainer_details` (`id_trainer`, `start_date`, `phone_number`, `work_area`, `bio`, `profile_picture`) VALUES
(24, '2005-02-14', '+48123123123', 'Wodzisław Śląski i okolice', 'Razem dojdziemy do twojej formy życia!', '93310536-628a-49e4-9d05-04bc28c3c0f8_DALL·E 2025-02-08 01.45.23 - A modern and minimalistic favicon for a fitness web application named \'GoGym\'. The design should be simple, clean, and recognizable at small sizes. It.webp'),
(27, '2019-02-14', '+48600500400', 'Województwo Śląskie / Online', NULL, '06ff994a-4de9-4a40-a14d-bde453fe654f_1.png'),
(28, '2022-10-22', '+48932648126', 'Mazowsze', NULL, NULL),
(29, '2018-03-03', '+48647374857', 'Województwo Śląskie', NULL, NULL);

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
-- Dumping data for table `trainer_experience`
--

INSERT INTO `trainer_experience` (`id`, `id_trainer`, `graduation_name`, `graduation_date`, `certification_file`) VALUES
(1, 24, 'Kurs Trenera Personalnego 2021 Gdańsk C.O.C.O', '2021-06-01', '67a0f93f-f67d-4088-a929-671c8a53af02_Trener-PL_Easy-Resize.com_.jpg');

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `trainer_reviews`
--

CREATE TABLE `trainer_reviews` (
  `id` int(11) NOT NULL,
  `trainer_id` int(11) NOT NULL,
  `client_id` int(11) NOT NULL,
  `rating` int(11) NOT NULL,
  `comment` text DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_polish_ci;

--
-- Dumping data for table `trainer_reviews`
--

INSERT INTO `trainer_reviews` (`id`, `trainer_id`, `client_id`, `rating`, `comment`, `created_at`) VALUES
(1, 24, 23, 5, 'Świetny trener! Najlepszy na rynku', '2025-03-02 01:29:52'),
(2, 27, 26, 5, 'Świetna trenerka! Poprowadziła mnie do niesamowitej formy', '2025-03-24 23:30:14');

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
-- Dumping data for table `trainer_specialization`
--

INSERT INTO `trainer_specialization` (`id`, `id_trainer`, `specialization`) VALUES
(1, 24, 'Ćwiczenia siłowe'),
(2, 24, 'CrossfFit'),
(3, 24, 'Redukcja masy'),
(5, 27, 'Trening siłowy'),
(6, 27, 'Kalistenika'),
(7, 27, 'Crossfit'),
(8, 28, 'Spalanie masy ciała'),
(9, 29, 'Kalistenika');

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
-- Dumping data for table `training_plans`
--

INSERT INTO `training_plans` (`id_plan`, `name`, `start_date`, `end_date`, `description`, `status`, `id_trainer`, `id_client`) VALUES
(1, 'Plan treningowy nr1 dla Jana Kowalskiego', '2025-03-02', '2025-03-04', 'Oto nasz pierwszy plan treningowy stworzony w celu rozbudowy twoich mięśni oraz utraty masy ciała.', 'active', 24, 23),
(4, 'Plan 2', '2025-03-03', '2025-03-06', 'Plan numer 2', 'active', 24, 23),
(5, 'Aaa', '2025-03-13', '2025-03-14', 'Aaa', 'completed', 24, 23),
(6, 'Plan treningowy 1 - Tomasz Nowak', '2025-03-27', '2025-03-31', 'Plan treningowy składa się z 5 dni. Dzień pierwszy skupia się na ćwiczeniach siłowych mających na celu rozbudowę mięśni. Zawiera podstawowe ruchy złożone, które angażują duże grupy mięśniowe.\nDzień drugi przeznaczony jest na trening górnych partii ciała, w tym ćwiczenia rozwijające klatkę piersiową, plecy oraz ramiona.\nTrzeciego dnia plan zakłada odpoczynek lub aktywność o niskiej intensywności, np. spacer lub joga.\nDzień czwarty to trening ukierunkowany na poprawę wytrzymałości mięśniowej poprzez ćwiczenia z większą liczbą powtórzeń i krótszym czasem przerwy.\nPiąty dzień koncentruje się na ćwiczeniach funkcjonalnych i mobilizujących, które mają poprawić ogólną sprawność ruchową i stabilizację ciała.\nPlan został ułożony z myślą o równomiernym rozwoju sylwetki, poprawie wydolności oraz minimalizacji ryzyka kontuzji.', 'active', 27, 26);

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
-- Dumping data for table `training_plan_days`
--

INSERT INTO `training_plan_days` (`id_day`, `day_type`, `status`, `notes`, `date`, `id_plan`) VALUES
(1, 'training', 'completed', 'W pierwszym dniu skupimy się na ćwiczeniach siłowych o dużym ciężarze.', '2025-03-02', 1),
(2, 'rest', 'notCompleted', 'Dzień drugi będzie dniem regeneracyjnym. Zaproponuje tutaj skorzystanie z bieżni.', '2025-03-03', 1),
(3, 'training', 'notCompleted', 'Trzeci dzień będzie opierał się na treningu dolnej partii ciała. Powodzenia', '2025-03-04', 1),
(6, 'training', 'completed', 'Dzień pierwszy', '2025-03-03', 4),
(7, 'training', 'completed', 'Dzień drugi', '2025-03-04', 4),
(8, 'rest', 'completed', 'Dzień trzeci', '2025-03-05', 4),
(9, 'training', 'notCompleted', 'Dzień czwarty', '2025-03-06', 4),
(10, 'training', 'completed', '', '2025-03-13', 5),
(11, 'rest', 'completed', '', '2025-03-14', 5),
(12, 'training', 'completed', 'Dzień pierwszy - siłowe (skup sie na dokładnym wykonaniu drugiego ćwiczenia, możesz wstawić potem wideo do analizy)', '2025-03-27', 6),
(13, 'training', 'notCompleted', 'Dzień drugi - górne partie ciała (pamiętaj o odpowiedniej postawie ciała oraz spięciu łopatek podczas wykonywania wszystkich ćwiczeń)', '2025-03-28', 6),
(14, 'rest', 'notCompleted', 'Dzień trzeci - odpocznij, pójdź na spacer i się trochę porozciągaj', '2025-03-29', 6),
(15, 'training', 'notCompleted', 'Dzień czwarty - dziś pracujemy na pełnych obrotach, w razie pytań co do ćwiczenia drugiego pisz na czacie.', '2025-03-30', 6),
(16, 'training', 'notCompleted', 'Dzień piąty - odrobina ruchu, zwiększymy twoją mobilność i gibkość', '2025-03-31', 6);

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
-- Dumping data for table `treiners_clients`
--

INSERT INTO `treiners_clients` (`id`, `id_trainer`, `id_client`) VALUES
(1, 24, 23),
(3, 27, 26);

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `user`
--

CREATE TABLE `user` (
  `id_user` int(11) NOT NULL,
  `email` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `first_name` varchar(30) NOT NULL,
  `last_name` varchar(30) NOT NULL,
  `birth_date` date NOT NULL,
  `gender` enum('KOBIETA','MĘŻCZYZNA') NOT NULL,
  `user_type` enum('CLIENT','TRAINER','ADMIN') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_polish_ci;

--
-- Dumping data for table `user`
--

INSERT INTO `user` (`id_user`, `email`, `password`, `first_name`, `last_name`, `birth_date`, `gender`, `user_type`) VALUES
(1, 'admin', '$2a$12$R.UqXcGt0N1TmqqyO6fce.S6fHUFcOWTqn/M6e89f2odVrcNWR/22', 'Admin', 'Admin', '2025-02-01', 'MĘŻCZYZNA', 'ADMIN'),
(23, 'client@gmail.com', '$2a$10$GFC0krRJ.emAq4aZtyVfqOGse9P/P.YfgbPlLt3MGMuOCPuM26Vke', 'Jan', 'Kowalski', '2001-10-02', 'MĘŻCZYZNA', 'CLIENT'),
(24, 'trainer@gmail.com', '$2a$10$hCzCl6nuZ5npYpwyQzLPnu.jps1o2XbxDhJg5WdtYIetay4SLEduS', 'Piotr', 'Nowak', '1990-01-01', 'MĘŻCZYZNA', 'TRAINER'),
(26, 'tomasz.nowak@gmail.com', '$2a$10$Ircsr9wQ3OaHK7blv0sXWODHgTEq.Nmpb6GVPWYuLx50/mYCNQKCK', 'Tomasz', 'Nowak', '2001-10-20', 'MĘŻCZYZNA', 'CLIENT'),
(27, 'anna.kowalska@gmail.com', '$2a$10$HuxjQmh0RZ9RpqPp9zgx/eaDwLHpVUyv0V/6FqZsNGeO6Hqmnu8zS', 'Anna', 'Kowalska', '1999-07-02', 'KOBIETA', 'TRAINER'),
(28, 'trener1@o2.pl', '$2a$10$Cmwt3veIMDpLoyKnQ51QvOHWf43SWNuCEElHIKRqCVwh5qK.HRzme', 'Adam', 'Podsiadło', '1981-01-01', 'MĘŻCZYZNA', 'TRAINER'),
(29, 'trener2@o2.pl', '$2a$10$q1vSI8soAwtQoKi5x2.M7ewKC3QZ00nS98kYYYR6H0EmkYxhkLwvG', 'Krystyna', 'Przybyła', '1995-02-02', 'KOBIETA', 'TRAINER');

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `user_badge`
--

CREATE TABLE `user_badge` (
  `id` int(11) NOT NULL,
  `id_user` int(11) NOT NULL,
  `badge_id` int(11) NOT NULL,
  `awarded_date` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_polish_ci;

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
  `id_user` int(11) NOT NULL,
  `id_day` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_polish_ci;

--
-- Dumping data for table `workouts`
--

INSERT INTO `workouts` (`id_workout`, `workout_date`, `intensity`, `notes`, `start_time`, `end_time`, `id_user`, `id_day`) VALUES
(1, '2025-02-28', 'medium', 'Trening na siłowni z obfitymi efektami. Forma była bardzo dobra', '14:55:00', '17:30:00', 23, NULL),
(2, '2025-03-02', 'high', 'Pierwszy dzień treningowy z planu trenera Piotra Nowaka', '18:00:00', '19:35:00', 23, 1),
(3, '2025-03-27', 'medium', 'Pomyślnie wypełniony pierwszy dzień planu', '13:00:00', '14:25:00', 26, 12);

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `workout_exercises`
--

CREATE TABLE `workout_exercises` (
  `id` int(11) NOT NULL,
  `sets` int(11) DEFAULT NULL,
  `reps` int(11) DEFAULT NULL,
  `weight` double DEFAULT NULL,
  `duration` int(11) DEFAULT NULL,
  `distance` double DEFAULT NULL,
  `id_workout` int(11) NOT NULL,
  `id_exercise` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_polish_ci;

--
-- Dumping data for table `workout_exercises`
--

INSERT INTO `workout_exercises` (`id`, `sets`, `reps`, `weight`, `duration`, `distance`, `id_workout`, `id_exercise`) VALUES
(1, 4, NULL, NULL, 150, NULL, 1, 30),
(2, 4, 8, 100, NULL, NULL, 1, 33),
(3, 5, 12, 65, NULL, NULL, 1, 54),
(4, 3, 10, NULL, NULL, NULL, 2, 1),
(5, 4, 8, 60, NULL, NULL, 2, 10),
(6, 3, 12, 45, NULL, NULL, 2, 12),
(7, 4, 10, 70, NULL, NULL, 3, 22),
(8, 2, NULL, NULL, 900, NULL, 3, 8);

--
-- Indeksy dla zrzutów tabel
--

--
-- Indeksy dla tabeli `badge`
--
ALTER TABLE `badge`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `name` (`name`);

--
-- Indeksy dla tabeli `body_part`
--
ALTER TABLE `body_part`
  ADD PRIMARY KEY (`id_bodypart`);

--
-- Indeksy dla tabeli `chat_rooms`
--
ALTER TABLE `chat_rooms`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user_id` (`user_id`),
  ADD KEY `trainer_id` (`trainer_id`);

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
-- Indeksy dla tabeli `messages`
--
ALTER TABLE `messages`
  ADD PRIMARY KEY (`id`),
  ADD KEY `chat_room_id` (`chat_room_id`),
  ADD KEY `sender_id` (`sender_id`);

--
-- Indeksy dla tabeli `notifications`
--
ALTER TABLE `notifications`
  ADD PRIMARY KEY (`id`),
  ADD KEY `id_user` (`id_user`);

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
-- Indeksy dla tabeli `trainer_reviews`
--
ALTER TABLE `trainer_reviews`
  ADD PRIMARY KEY (`id`),
  ADD KEY `trainer_id` (`trainer_id`),
  ADD KEY `client_id` (`client_id`);

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
-- Indeksy dla tabeli `user_badge`
--
ALTER TABLE `user_badge`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_user_badge_user` (`id_user`),
  ADD KEY `fk_user_badge_badge` (`badge_id`);

--
-- Indeksy dla tabeli `workouts`
--
ALTER TABLE `workouts`
  ADD PRIMARY KEY (`id_workout`),
  ADD KEY `fk_workouts_user` (`id_user`),
  ADD KEY `fk_workout_training_plan_day` (`id_day`);

--
-- Indeksy dla tabeli `workout_exercises`
--
ALTER TABLE `workout_exercises`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_workout_exercises_workout` (`id_workout`),
  ADD KEY `fk_workout_exercises_exercise` (`id_exercise`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `badge`
--
ALTER TABLE `badge`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT for table `body_part`
--
ALTER TABLE `body_part`
  MODIFY `id_bodypart` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=17;

--
-- AUTO_INCREMENT for table `chat_rooms`
--
ALTER TABLE `chat_rooms`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT for table `client_details`
--
ALTER TABLE `client_details`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `equipment`
--
ALTER TABLE `equipment`
  MODIFY `id_equipment` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=20;

--
-- AUTO_INCREMENT for table `exercise`
--
ALTER TABLE `exercise`
  MODIFY `id_exercise` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=94;

--
-- AUTO_INCREMENT for table `messages`
--
ALTER TABLE `messages`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT for table `notifications`
--
ALTER TABLE `notifications`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=38;

--
-- AUTO_INCREMENT for table `plan_exercises`
--
ALTER TABLE `plan_exercises`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=22;

--
-- AUTO_INCREMENT for table `requests`
--
ALTER TABLE `requests`
  MODIFY `id_request` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT for table `trainer_experience`
--
ALTER TABLE `trainer_experience`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `trainer_reviews`
--
ALTER TABLE `trainer_reviews`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `trainer_specialization`
--
ALTER TABLE `trainer_specialization`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT for table `training_plans`
--
ALTER TABLE `training_plans`
  MODIFY `id_plan` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `training_plan_days`
--
ALTER TABLE `training_plan_days`
  MODIFY `id_day` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=17;

--
-- AUTO_INCREMENT for table `treiners_clients`
--
ALTER TABLE `treiners_clients`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `user`
--
ALTER TABLE `user`
  MODIFY `id_user` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=30;

--
-- AUTO_INCREMENT for table `user_badge`
--
ALTER TABLE `user_badge`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `workouts`
--
ALTER TABLE `workouts`
  MODIFY `id_workout` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `workout_exercises`
--
ALTER TABLE `workout_exercises`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `chat_rooms`
--
ALTER TABLE `chat_rooms`
  ADD CONSTRAINT `chat_rooms_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id_user`),
  ADD CONSTRAINT `chat_rooms_ibfk_2` FOREIGN KEY (`trainer_id`) REFERENCES `user` (`id_user`);

--
-- Constraints for table `client_details`
--
ALTER TABLE `client_details`
  ADD CONSTRAINT `client_details_ibfk_1` FOREIGN KEY (`id_user`) REFERENCES `user` (`id_user`) ON DELETE CASCADE;

--
-- Constraints for table `exercise_bodypart`
--
ALTER TABLE `exercise_bodypart`
  ADD CONSTRAINT `exercise_bodypart_ibfk_1` FOREIGN KEY (`id_exercise`) REFERENCES `exercise` (`id_exercise`) ON DELETE CASCADE,
  ADD CONSTRAINT `exercise_bodypart_ibfk_2` FOREIGN KEY (`id_bodypart`) REFERENCES `body_part` (`id_bodypart`) ON DELETE CASCADE;

--
-- Constraints for table `exercise_equipment`
--
ALTER TABLE `exercise_equipment`
  ADD CONSTRAINT `exercise_equipment_ibfk_1` FOREIGN KEY (`id_exercise`) REFERENCES `exercise` (`id_exercise`) ON DELETE CASCADE,
  ADD CONSTRAINT `exercise_equipment_ibfk_2` FOREIGN KEY (`id_equipment`) REFERENCES `equipment` (`id_equipment`) ON DELETE CASCADE;

--
-- Constraints for table `messages`
--
ALTER TABLE `messages`
  ADD CONSTRAINT `messages_ibfk_1` FOREIGN KEY (`chat_room_id`) REFERENCES `chat_rooms` (`id`),
  ADD CONSTRAINT `messages_ibfk_2` FOREIGN KEY (`sender_id`) REFERENCES `user` (`id_user`);

--
-- Constraints for table `notifications`
--
ALTER TABLE `notifications`
  ADD CONSTRAINT `notifications_ibfk_1` FOREIGN KEY (`id_user`) REFERENCES `user` (`id_user`) ON DELETE CASCADE;

--
-- Constraints for table `plan_exercises`
--
ALTER TABLE `plan_exercises`
  ADD CONSTRAINT `fk_day` FOREIGN KEY (`id_day`) REFERENCES `training_plan_days` (`id_day`) ON DELETE CASCADE,
  ADD CONSTRAINT `fk_plan_exercises_exercise` FOREIGN KEY (`id_exercise`) REFERENCES `exercise` (`id_exercise`) ON DELETE CASCADE,
  ADD CONSTRAINT `fk_plan_exercises_plan` FOREIGN KEY (`id_plan`) REFERENCES `training_plans` (`id_plan`) ON DELETE CASCADE;

--
-- Constraints for table `requests`
--
ALTER TABLE `requests`
  ADD CONSTRAINT `requests_ibfk_1` FOREIGN KEY (`id_trainer`) REFERENCES `user` (`id_user`) ON DELETE CASCADE,
  ADD CONSTRAINT `requests_ibfk_2` FOREIGN KEY (`id_client`) REFERENCES `user` (`id_user`) ON DELETE CASCADE;

--
-- Constraints for table `trainer_details`
--
ALTER TABLE `trainer_details`
  ADD CONSTRAINT `trainer_details_ibfk_1` FOREIGN KEY (`id_trainer`) REFERENCES `user` (`id_user`) ON DELETE CASCADE;

--
-- Constraints for table `trainer_experience`
--
ALTER TABLE `trainer_experience`
  ADD CONSTRAINT `trainer_experience_ibfk_1` FOREIGN KEY (`id_trainer`) REFERENCES `user` (`id_user`) ON DELETE CASCADE;

--
-- Constraints for table `trainer_reviews`
--
ALTER TABLE `trainer_reviews`
  ADD CONSTRAINT `trainer_reviews_ibfk_1` FOREIGN KEY (`trainer_id`) REFERENCES `user` (`id_user`) ON DELETE CASCADE,
  ADD CONSTRAINT `trainer_reviews_ibfk_2` FOREIGN KEY (`client_id`) REFERENCES `user` (`id_user`) ON DELETE CASCADE;

--
-- Constraints for table `trainer_specialization`
--
ALTER TABLE `trainer_specialization`
  ADD CONSTRAINT `trainer_specialization_ibfk_1` FOREIGN KEY (`id_trainer`) REFERENCES `trainer_details` (`id_trainer`) ON DELETE CASCADE;

--
-- Constraints for table `training_plans`
--
ALTER TABLE `training_plans`
  ADD CONSTRAINT `fk_training_plans_client` FOREIGN KEY (`id_client`) REFERENCES `user` (`id_user`) ON DELETE CASCADE,
  ADD CONSTRAINT `fk_training_plans_trainer` FOREIGN KEY (`id_trainer`) REFERENCES `user` (`id_user`) ON DELETE CASCADE;

--
-- Constraints for table `training_plan_days`
--
ALTER TABLE `training_plan_days`
  ADD CONSTRAINT `training_plan_days_ibfk_1` FOREIGN KEY (`id_plan`) REFERENCES `training_plans` (`id_plan`) ON DELETE CASCADE;

--
-- Constraints for table `treiners_clients`
--
ALTER TABLE `treiners_clients`
  ADD CONSTRAINT `fk_trainers_clients_client` FOREIGN KEY (`id_client`) REFERENCES `user` (`id_user`) ON DELETE CASCADE,
  ADD CONSTRAINT `fk_trainers_clients_trainer` FOREIGN KEY (`id_trainer`) REFERENCES `user` (`id_user`) ON DELETE CASCADE;

--
-- Constraints for table `user_badge`
--
ALTER TABLE `user_badge`
  ADD CONSTRAINT `fk_user_badge_badge` FOREIGN KEY (`badge_id`) REFERENCES `badge` (`id`),
  ADD CONSTRAINT `fk_user_badge_user` FOREIGN KEY (`id_user`) REFERENCES `user` (`id_user`);

--
-- Constraints for table `workouts`
--
ALTER TABLE `workouts`
  ADD CONSTRAINT `fk_workout_training_plan_day` FOREIGN KEY (`id_day`) REFERENCES `training_plan_days` (`id_day`) ON DELETE SET NULL,
  ADD CONSTRAINT `fk_workouts_user` FOREIGN KEY (`id_user`) REFERENCES `user` (`id_user`) ON DELETE CASCADE;

--
-- Constraints for table `workout_exercises`
--
ALTER TABLE `workout_exercises`
  ADD CONSTRAINT `fk_workout_exercises_exercise` FOREIGN KEY (`id_exercise`) REFERENCES `exercise` (`id_exercise`) ON DELETE CASCADE,
  ADD CONSTRAINT `fk_workout_exercises_workout` FOREIGN KEY (`id_workout`) REFERENCES `workouts` (`id_workout`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
