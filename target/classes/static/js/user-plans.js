    let originalPlansHTML = document.querySelector(".plans-grid").innerHTML;
    /* ==================== Inicjalizacja ==================== */
    document.addEventListener("DOMContentLoaded", () => {
      initFilters();
      initModals();
      initStatusHandlers();
      initVideoHandlers();
      initWorkoutHandlers();
      initDurationFormatting();
    });

    /* ==================== Filtry i sortowanie ==================== */
    function initFilters() {
      document.getElementById("searchInput").addEventListener("keyup", filterPlans);
      document.getElementById("dateFilter").addEventListener("change", filterPlans);
      document.getElementById("trainerSelect").addEventListener("change", filterPlans);
      document.getElementById("sortSelect").addEventListener("change", sortPlans);
      document.getElementById("hideCompletedPlans").addEventListener("change", filterPlans);
      document.querySelector(".reset-button").addEventListener("click", resetFilters);
    }

    function filterPlans() {
      const input = document.getElementById("searchInput").value.toLowerCase();
      const selectedDate = document.getElementById("dateFilter").value;
      const selectedTrainer = document.getElementById("trainerSelect").value.toLowerCase();
      const plans = document.querySelectorAll(".plan-each");
      const hideCompleted = document.getElementById("hideCompletedPlans").checked;
      plans.forEach(plan => {
        const planName = plan.querySelector(".plan-name").innerText.toLowerCase();
        const startDate = plan.querySelector(".start-date").innerText.trim();
        const trainerName = plan.querySelector(".trainer-name").innerText.toLowerCase();
        const statusText = plan.querySelector(".status-text").innerText.toLowerCase();
        const isCompleted = statusText.includes("ukończony");
        const matchesSearch = planName.includes(input);
        const matchesDate = selectedDate === "" || startDate === selectedDate;
        const matchesTrainer = selectedTrainer === "" || trainerName.includes(selectedTrainer);
        plan.style.display = (matchesSearch && matchesDate && matchesTrainer && (!hideCompleted || !isCompleted)) ? "block" : "none";
      });
    }

    function sortPlans() {
      const sortBy = document.getElementById("sortSelect").value;
      const container = document.querySelector(".plans-grid");
      const plans = Array.from(document.querySelectorAll(".plan-each"));
      plans.sort((a, b) => {
        if (sortBy === "name") {
          const nameA = a.querySelector(".plan-name").innerText;
          const nameB = b.querySelector(".plan-name").innerText;
          return naturalSort(nameA, nameB);
        } else if (sortBy === "startDate") {
          const dateA = new Date(a.querySelector(".start-date").innerText.trim());
          const dateB = new Date(b.querySelector(".start-date").innerText.trim());
          return dateA - dateB;
        } else if (sortBy === "status") {
          return a.querySelector(".status-text").innerText.localeCompare(b.querySelector(".status-text").innerText);
        }
      });
      container.innerHTML = "";
      plans.forEach(plan => container.appendChild(plan));
    }

    function naturalSort(a, b) {
      return a.localeCompare(b, undefined, { numeric: true, sensitivity: 'base' });
    }

    function resetFilters() {
      document.getElementById("searchInput").value = "";
      document.getElementById("dateFilter").value = "";
      document.getElementById("sortSelect").value = "";
      document.getElementById("trainerSelect").value = "";
      document.getElementById("hideCompletedPlans").checked = false;
      const container = document.querySelector(".plans-grid");
      container.innerHTML = originalPlansHTML;
      document.querySelectorAll(".plan-each").forEach(plan => plan.style.display = "block");
      // Dodaj ponowne zainicjowanie workout handlerów:
      initWorkoutHandlers();
    }

    /* ==================== Modale ==================== */
    function initModals() {
      document.addEventListener("click", (event) => {
        const panel = event.target.closest(".plan-panel");
        if (panel && !event.target.closest("button, a")) {
          const planId = panel.getAttribute("data-plan-id");
          const modal = document.querySelector(`.modal[data-plan-id="${planId}"]`);
          if (modal) modal.style.display = "flex";
        }
        if (event.target.classList.contains("close-modal") || event.target.classList.contains("modal")) {
          const modal = event.target.closest(".modal");
          if (modal) modal.style.display = "none";
        }
      });
      document.addEventListener("keydown", (event) => {
        if (event.key === "Escape") {
          document.querySelectorAll(".modal").forEach(modal => modal.style.display = "none");
        }
      });
    }

    /* ==================== Status Handlers ==================== */
    function initStatusHandlers() {
      document.addEventListener("click", (event) => {
        if (event.target.matches(".toggle-exercise-status")) {
          handleExerciseStatusToggle(event.target);
        }
        if (event.target.matches(".toggle-rest-day-status")) {
          handleRestDayStatusToggle(event.target);
        }
      });
    }

    function handleExerciseStatusToggle(button) {
      const exerciseId = button.getAttribute("data-exercise-id");
      const currentStatus = !button.textContent.includes("Cofnij");
      fetch(`/update-exercise-status/${exerciseId}?completed=${currentStatus}`, { method: "POST" })
        .then(checkStatus)
        .then(() => updateExerciseUI(button, currentStatus))
        .catch(console.error);
    }

function updateExerciseUI(button, status) {
  button.textContent = status ? "Cofnij ukończenie" : "Oznacz jako wykonane";
  const exerciseElem = button.closest(".exercise");
  // Zakładamy, że element wyświetlający status ćwiczenia ma klasę "status-badge"
  const statusContainer = exerciseElem.querySelector(".status-badge");
  if (statusContainer) {
    if (status) {
      statusContainer.classList.remove("notCompleted");
      statusContainer.classList.add("completed");
      statusContainer.textContent = "UKOŃCZONY";
    } else {
      statusContainer.classList.remove("completed");
      statusContainer.classList.add("notCompleted");
      statusContainer.textContent = "NIEUKOŃCZONE";
    }
  }
  const dayElement = button.closest("[data-day-id]");
  if (!dayElement) return;
  const dayId = dayElement.getAttribute("data-day-id");
  fetch(`/update-day-status/${dayId}`, { method: "POST" })
    .then(checkStatus)
    .then(response => response.json())
    .then(data => {
      updateDayUI(data);
      updatePlanStatusFromElement(button);
    })
    .catch(console.error);
}


    function handleRestDayStatusToggle(button) {
      const dayId = button.getAttribute("data-day-id");
      fetch(`/update-rest-day-status/${dayId}`, { method: "POST" })
        .then(checkStatus)
        .then(response => response.json())
        .then(data => {
          updateRestDayUI(button, data);
        })
        .catch(console.error);
    }

function updateDayUI(data) {
  // Załóżmy, że w elemencie dnia status wyświetlany jest jako badge
  const dayStatusElement = document.querySelector(`[data-day-id="${data.dayId}"] .status-badge`);
  if (dayStatusElement) {
    if (data.status === "completed") {
      dayStatusElement.classList.remove("active");
      dayStatusElement.classList.add("completed");
      dayStatusElement.textContent = "UKOŃCZONY";
    } else {
      dayStatusElement.classList.remove("completed");
      dayStatusElement.classList.add("active");
      dayStatusElement.textContent = "AKTYWNY";
    }
  }
}


function updateRestDayUI(button, data) {
  button.textContent = data.status === "completed" ? "Cofnij ukończenie" : "Oznacz jako wykonane";
  const dayElement = document.querySelector(`[data-day-id="${data.dayId}"]`);
  if (!dayElement) return;
  const statusContainer = dayElement.querySelector(".status-badge");
  if (statusContainer) {
    if (data.status === "completed") {
      statusContainer.classList.remove("active");
      statusContainer.classList.add("completed");
      statusContainer.textContent = "UKOŃCZONY";
    } else {
      statusContainer.classList.remove("completed");
      statusContainer.classList.add("active");
      statusContainer.textContent = "AKTYWNY";
    }
  }
  updatePlanStatusFromElement(button);
}


    function updatePlanStatusFromElement(element) {
      let planId = null;
      const modal = element.closest(".modal");
      if (modal) {
        planId = modal.getAttribute("data-plan-id");
      } else {
        const planEach = element.closest(".plan-each");
        if (planEach) {
          planId = planEach.getAttribute("data-plan-id");
        }
      }
      if (!planId) return;
      fetch(`/update-plan-status/${planId}`, { method: "POST" })
        .then(checkStatus)
        .then(response => response.json())
        .then(data => updatePlanUI(data))
        .catch(console.error);
    }

function updatePlanUI(data) {
  const planStatusElement = document.querySelector(`.modal[data-plan-id="${data.planId}"] #plan-status`);
  if (planStatusElement) {
    if (data.status === "completed") {
      planStatusElement.classList.remove("active");
      planStatusElement.classList.add("completed");
      planStatusElement.textContent = "UKOŃCZONY";
    } else {
      planStatusElement.classList.remove("completed");
      planStatusElement.classList.add("active");
      planStatusElement.textContent = "AKTYWNY";
    }
  }
  const planPanel = document.querySelector(`.plan-panel[data-plan-id="${data.planId}"] .status-text`);
  if (planPanel) {
    if (data.status === "completed") {
      planPanel.textContent = "UKOŃCZONY";
      planPanel.classList.remove("active");
      planPanel.classList.add("completed");
      planPanel.style.backgroundColor = "#28a745"; // zielony
    } else {
      planPanel.textContent = "AKTYWNY";
      planPanel.classList.remove("completed");
      planPanel.classList.add("active");
      planPanel.style.backgroundColor = "#ffc107"; // żółty
    }
  }
}


    /* ---------- Funkcja checkStatus ---------- */
    function checkStatus(response) {
      if (!response.ok) {
        throw new Error("Network response was not ok");
      }
      return response;
    }

    /* ==================== Duration Formatting ==================== */
    function initDurationFormatting() {
      document.querySelectorAll(".exercise-duration").forEach(el => {
        const seconds = parseInt(el.getAttribute("data-seconds"), 10);
        el.textContent = formatDuration(seconds);
      });
    }

    function formatDuration(seconds) {
      if (isNaN(seconds) || seconds === null) return "Brak danych";
      const hours = Math.floor(seconds / 3600);
      const minutes = Math.floor((seconds % 3600) / 60);
      const remainingSeconds = seconds % 60;
      return hours > 0
        ? `${String(hours).padStart(2, "0")}:${String(minutes).padStart(2, "0")}:${String(remainingSeconds).padStart(2, "0")}`
        : `${String(minutes).padStart(2, "0")}:${String(remainingSeconds).padStart(2, "0")}`;
    }

    /* ==================== Workout Modal Handlers ==================== */
    function initWorkoutHandlers() {
      document.querySelectorAll(".add-to-workout-btn").forEach(button => {
        const dayId = button.getAttribute("data-day-id");
        fetch(`/workouts/check-workout-exists/${dayId}`)
          .then(response => response.json())
          .then(exists => {
            button.textContent = exists ? "Usuń dzień z workoutów" : "Dodaj dzień do workoutów";
          })
          .catch(error => {
            console.error("Błąd sprawdzania workoutu:", error);
            button.textContent = "Dodaj dzień do workoutów";
          });
        button.addEventListener("click", function () {
          const isRemoving = this.textContent.includes("Usuń");
          if (isRemoving) {
            fetch(`/workouts/remove-workout-from-day?dayId=${dayId}`, { method: "POST" })
              .then(checkStatus)
              .then(response => response.text())
              .then(() => {
                alert("Workout został usunięty!");
                this.textContent = "Dodaj dzień do workoutów";
              })
              .catch(console.error);
          } else {
            fetch(`/get-training-day/${dayId}`)
              .then(response => response.json())
              .then(day => {
                document.getElementById("workoutModal").setAttribute("data-day-id", dayId);
                document.getElementById("workoutDate").value = day.date;
                document.getElementById("exerciseList").innerHTML = "";
                const completedExercises = day.exercises.filter(ex => ex.status === "completed");
                if (completedExercises.length === 0) {
                  alert("Nie masz ukończonych ćwiczeń w tym dniu!");
                  return;
                }
                completedExercises.forEach(ex => {
                  const exerciseRow = document.createElement("div");
                  let details = "";
                  if(ex.exercise?.type === "CARDIO"){
                    const durationSeconds = parseInt(ex.duration, 10);
                    const formattedDuration = formatDuration(durationSeconds);
                    details = `${ex.sets} serie, ${formattedDuration}, ${ex.distance ?? "Brak dystansu"} km`;
                  } else {
                    details = `${ex.sets}x${ex.reps}, ${ex.weight ?? "bez obciążenia"} kg`;
                  }
                  exerciseRow.innerHTML = `<p><strong>${ex.exercise?.name || "Nieznane ćwiczenie"}</strong> - ${details}</p>`;
                  document.getElementById("exerciseList").appendChild(exerciseRow);
                });
                document.getElementById("workoutModal").style.display = "flex";
              })
              .catch(console.error);
          }
        });
      });

      document.getElementById("saveWorkout").addEventListener("click", function () {
        const startTime = document.getElementById("startTime").value;
        const endTime = document.getElementById("endTime").value;
        const intensity = document.getElementById("intensity").value;
        const notes = document.getElementById("notes").value;
        const dayId = document.getElementById("workoutModal").getAttribute("data-day-id");
        if (!startTime || !endTime) {
          alert("Musisz podać godzinę rozpoczęcia i zakończenia treningu!");
          return;
        }
        const workoutData = { dayId, startTime, endTime, intensity, notes };
        fetch("/workouts/add-workout-from-day", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(workoutData)
        })
          .then(checkStatus)
          .then(response => response.text())
          .then(() => {
            alert("Workout został dodany!");
            document.getElementById("workoutModal").style.display = "none";
            document.querySelector(`.add-to-workout-btn[data-day-id="${dayId}"]`).textContent = "Usuń dzień z workoutów";
          })
          .catch(console.error);
        resetWorkoutModal();
      });
      document.getElementById("closeWorkoutModal").addEventListener("click", resetWorkoutModal);
    }

    function resetWorkoutModal() {
      document.getElementById("workoutModal").removeAttribute("data-day-id");
      document.getElementById("workoutDate").value = "";
      document.getElementById("startTime").value = "";
      document.getElementById("endTime").value = "";
      document.getElementById("intensity").value = "medium";
      document.getElementById("notes").value = "";
      document.getElementById("exerciseList").innerHTML = "";
      document.getElementById("workoutModal").style.display = "none";
    }

    /* ==================== Video Upload Handlers ==================== */
    function initVideoHandlers() {
      attachUploadEvents();
      attachDeleteEvents();
    }

    function attachUploadEvents() {
      document.querySelectorAll(".upload-video-btn").forEach(button => {
        button.onclick = function () {
          const exerciseId = this.previousElementSibling.getAttribute("data-exercise-id");
          const fileInput = this.previousElementSibling;
          const file = fileInput.files[0];
          if (!file) {
            alert("Proszę wybrać plik wideo.");
            return;
          }
          if (file.size > 100 * 1024 * 1024) {
            alert("Plik jest za duży! Maksymalny rozmiar to 100MB. Możesz dodać link do wideo zamiast przesyłać plik.");
            return;
          }
          showLoadingSpinner(exerciseId);
          const formData = new FormData();
          formData.append("file", file);
          fetch(`/video/upload/${exerciseId}`, { method: "POST", body: formData })
            .then(response => response.text())
            .then(videoUrl => replaceWithVideo(exerciseId, videoUrl, "cloudinary"))
            .catch(console.error)
            .finally(() => setTimeout(() => hideLoadingSpinner(exerciseId), 50));
        };
      });
      document.querySelectorAll(".add-link-btn").forEach(button => {
        button.onclick = function () {
          const exerciseId = this.previousElementSibling.getAttribute("data-exercise-id");
          const linkInput = this.previousElementSibling;
          const link = linkInput.value.trim();
          if (!link) {
            alert("Proszę wpisać link do wideo.");
            return;
          }
          showLoadingSpinner(exerciseId);
          fetch(`/video/link/${exerciseId}`, {
            method: "POST",
            headers: { "Content-Type": "application/x-www-form-urlencoded" },
            body: "link=" + encodeURIComponent(link)
          })
            .then(response => response.text())
            .then(videoUrl => replaceWithVideo(exerciseId, videoUrl, "link"))
            .catch(console.error)
            .finally(() => setTimeout(() => hideLoadingSpinner(exerciseId), 50));
        };
      });
    }

    function attachDeleteEvents() {
      document.querySelectorAll(".delete-video-btn").forEach(button => {
        button.onclick = function () {
          const exerciseId = this.getAttribute("data-exercise-id");
          showLoadingSpinner(exerciseId);
          fetch(`/video/delete/${exerciseId}`, { method: "DELETE" })
            .then(response => response.text())
            .then(() => restoreUploadSection(exerciseId))
            .catch(console.error)
            .finally(() => setTimeout(() => hideLoadingSpinner(exerciseId), 50));
        };
      });
    }

    function replaceWithVideo(exerciseId, videoUrl, type) {
      const exerciseElement = document.querySelector(`.exercise [data-exercise-id="${exerciseId}"]`).closest(".exercise");
      const videoSection = exerciseElement.querySelector(".video-upload-section");
      if (videoSection) videoSection.remove();
      const oldVideoStatus = exerciseElement.querySelector(".video-preview");
      if (oldVideoStatus) oldVideoStatus.remove();
      const videoStatusDiv = document.createElement("div");
      videoStatusDiv.classList.add("video-preview");
      if (type === "cloudinary") {
        videoStatusDiv.innerHTML = `
          <video src="${videoUrl}" controls width="300">
            Twoja przeglądarka nie obsługuje odtwarzacza wideo.
          </video>
          <button type="button" class="delete-video-btn" style="padding:5px 10px; font-size:13px;" data-exercise-id="${exerciseId}">Usuń nagranie</button>
        `;
      } else {
        videoStatusDiv.innerHTML = `
          <p><a href="${videoUrl}" target="_blank">🔗 Obejrzyj nagranie</a></p>
          <button type="button" class="delete-video-btn" data-exercise-id="${exerciseId}" style="padding:5px 10px; font-size:13px;">Usuń nagranie</button>
        `;
      }
      exerciseElement.appendChild(videoStatusDiv);
      attachDeleteEvents();
    }

    function restoreUploadSection(exerciseId) {
      const exerciseElement = document.querySelector(`.exercise [data-exercise-id="${exerciseId}"]`).closest(".exercise");
      const oldVideoStatus = exerciseElement.querySelector(".video-preview");
      if (oldVideoStatus) oldVideoStatus.remove();
      const videoUploadHtml = `
        <div class="video-upload-section" style="margin-top:10px;">
        <input type="file" class="video-input" data-exercise-id="${exerciseId}" accept="video/*">
        <button type="button" class="upload-video-btn" style="padding:5px 10px; font-size:13px;">Dodaj film</button>
        <input type="text" class="video-link-input" data-exercise-id="${exerciseId}" placeholder="Lub wklej link do filmu">
        <button type="button" class="add-link-btn" style="padding:5px 10px; font-size:13px;">Dodaj link</button>
        </div>
        <div class="loading-spinner" id="spinner-${exerciseId}"></div>
      `;
      exerciseElement.insertAdjacentHTML("beforeend", videoUploadHtml);
      attachUploadEvents();
    }

    function showLoadingSpinner(exerciseId) {
      const exerciseElement = document.querySelector(`.exercise [data-exercise-id="${exerciseId}"]`).closest(".exercise");
      let spinner = exerciseElement.querySelector(".loading-spinner");
      if (!spinner) {
        spinner = document.createElement("div");
        spinner.classList.add("loading-spinner");
        spinner.setAttribute("id", `spinner-${exerciseId}`);
        exerciseElement.appendChild(spinner);
      }
      spinner.style.display = "block";
    }

    function hideLoadingSpinner(exerciseId) {
      const spinner = document.getElementById(`spinner-${exerciseId}`);
      if (spinner) {
        spinner.style.display = "none";
        spinner.remove();
      }
    }