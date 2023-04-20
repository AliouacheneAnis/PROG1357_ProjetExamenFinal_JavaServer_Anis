// Variables pour manipuler Fauna 
var faunadb = window.faunadb;
var q = faunadb.query;

// Variables pour les données d'eau et de qualité
var Levelwater, Quality;

// Client FaunaDB
var client = new faunadb.Client({

  secret: 'fnAE_mGgjoACTTxwwwhPkM7akuitNGHl-aZHIRMV', // clé d'accès à la base de données FaunaDB
  endpoint: 'https://db.fauna.com/', // l'URL de l'endpoint Fauna
});

// Requête pour obtenir les données de FaunaDB et afficher les résultats dans le tableau HTML
client.query(
  q.Paginate(q.Match(q.Index("data_sort")), { size: 1 })
)
.then(function (res) { 
  const tableBody = document.getElementById('data-table');
  const row = document.createElement('tr');
  row.innerHTML = `
    <td>${res.data[0][1]}</td>
    <td>${res.data[0][2]}</td>
  `;
  tableBody.appendChild(row);
  console.log('Result:', res.data[0][2]) ;
})
.catch(function (err) { console.log('Error:', err) });

// Fonction pour obtenir les données de FaunaDB
async function getData() {
  const response = await client.query(
    q.Paginate(q.Match(q.Index("data_sort_desc")))
  );

  // Extraction des données de niveau d'eau et de qualité vers des tableau pour les afficher apres dans un graph 
  const t = [];
  const waterLevelData = []; 
  const waterQualityData = [];
  for (let index = 0; index < response.data.length; index++) {
    t[index] = new Date(response.data[index][0]);
    waterLevelData[index] = response.data[index][1]; 
    waterQualityData[index] = response.data[index][2];
  }

  // Affichage des données dans le graph
  const ctx1 = document.getElementById('chart1').getContext('2d');
  const chart1 = new Chart(ctx1, {
    type: 'line',
    data: {
      labels: waterQualityData,
      datasets: [{
        label: 'Water Level',
        data: waterLevelData, 
        backgroundColor: 'rgba(0, 0, 0, 0)', // Définir la couleur de fond du graphique comme transparente
        borderColor: 'blue', // Définir la couleur de la bordure du graphique comme bleue
        borderWidth: 1 // Définir la largeur de la bordure du graphique comme 1
      }]
    },
    options: {
      scales: {
        yAxes: [{
          position: 'right', 
          ticks: {
            beginAtZero: true // Définir la valeur minimale de l'axe y comme zéro
          }
        }]
      }
    }
  });

  // Actualisation des données toutes les 6 secondes
  setInterval(function() {
    location.reload();
  }, 6000);
}

// Appeler getData() initialement pour afficher les données et les graphiques initiaux
getData();