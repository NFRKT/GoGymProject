    document.addEventListener('DOMContentLoaded', function () {
      document.querySelectorAll('.plan-panel').forEach(panel => {
        panel.addEventListener('click', (event) => {
          if (event.target.closest("button") || event.target.closest("a")) return;
          const planId = panel.getAttribute('data-plan-id');
          const modal = document.querySelector(`.modal[data-plan-id="${planId}"]`);
          modal.style.display = 'flex';
        });
      });
      document.addEventListener('click', (event) => {
        const modal = event.target.closest('.modal');
        if (event.target.classList.contains('close-modal') || (modal && event.target === modal)) {
          closeModal(modal);
        }
      });
      document.addEventListener('keydown', (event) => {
        if (event.key === "Escape") {
          document.querySelectorAll('.modal').forEach(closeModal);
        }
      });
      function closeModal(modal) {
        if (modal) modal.style.display = 'none';
      }
    });
    function deletePlan(button) {
      let planId = button.getAttribute("data-plan-id");
      if (confirm("Czy na pewno chcesz usunąć ten plan?")) {
        fetch(`/trainer-plans/delete/${planId}`, { method: "POST" })
          .then(response => {
            if (response.ok) {
              alert("Plan został usunięty.");
              location.reload();
            } else {
              return response.text().then(text => { throw new Error(text); });
            }
          })
          .catch(error => {
            console.error("Błąd podczas usuwania planu:", error);
            alert("Wystąpił błąd podczas usuwania planu.");
          });
      }
    }
    function filterPlans() {
      let input = document.getElementById("searchInput").value.toLowerCase();
      let selectedDate = document.getElementById("dateFilter").value;
      let selectedClient = document.getElementById("clientSelect").value.toLowerCase();
      let plans = document.querySelectorAll(".plan-each");
      let hideCompleted = document.getElementById("hideCompletedPlans").checked;
      plans.forEach(plan => {
        let planName = plan.querySelector(".plan-name").innerText.toLowerCase();
        let startDate = plan.querySelector(".start-date").innerText.trim();
        let clientName = plan.querySelector(".client-name").innerText.toLowerCase();
        let statusText = plan.querySelector(".status-text").innerText.toLowerCase();
        let isCompleted = statusText.includes("ukończony");
        let matchesSearch = planName.includes(input);
        let matchesDate = selectedDate === "" || startDate === selectedDate;
        let matchesClient = selectedClient === "" || clientName.includes(selectedClient);
        plan.style.display = (matchesSearch && matchesDate && matchesClient && (!hideCompleted || !isCompleted)) ? "block" : "none";
      });
    }
    function sortPlans() {
      let sortBy = document.getElementById("sortSelect").value;
      document.querySelectorAll(".plans-grid").forEach(container => {
        let plans = Array.from(container.querySelectorAll(".plan-each"));
        plans.sort((a, b) => {
          if (sortBy === "name") {
            return a.querySelector(".plan-name").innerText.localeCompare(b.querySelector(".plan-name").innerText, undefined, { numeric: true, sensitivity: 'base' });
          } else if (sortBy === "startDate") {
            return new Date(a.querySelector(".start-date").innerText.trim()) - new Date(b.querySelector(".start-date").innerText.trim());
          } else if (sortBy === "status") {
            return a.querySelector(".status-text").innerText.localeCompare(b.querySelector(".status-text").innerText);
          }
        });
        container.innerHTML = '';
        plans.forEach(plan => container.appendChild(plan));
      });
    }
    function resetFilters() {
      document.getElementById("searchInput").value = "";
      document.getElementById("dateFilter").value = "";
      document.getElementById("sortSelect").value = "";
      document.getElementById("clientSelect").value = "";
      document.getElementById("hideCompletedPlans").checked = false;
      document.querySelectorAll(".plan-each").forEach(plan => plan.style.display = "block");
    }
    function formatDuration(seconds) {
      if (isNaN(seconds) || seconds === null) return "Brak danych";
      let hours = Math.floor(seconds / 3600);
      let minutes = Math.floor((seconds % 3600) / 60);
      let remainingSeconds = seconds % 60;
      return hours > 0
        ? `${String(hours).padStart(2, '0')}:${String(minutes).padStart(2, '0')}:${String(remainingSeconds).padStart(2, '0')}`
        : `${String(minutes).padStart(2, '0')}:${String(remainingSeconds).padStart(2, '0')}`;
    }
    document.addEventListener("DOMContentLoaded", function () {
      document.querySelectorAll(".exercise-duration").forEach(el => {
        let seconds = parseInt(el.getAttribute("data-seconds"), 10);
        el.textContent = formatDuration(seconds);
      });
    });