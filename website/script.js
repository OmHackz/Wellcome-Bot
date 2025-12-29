document.getElementById('downloadBtn').addEventListener('click', () => {
  const status = document.getElementById('downloadStatus')
  const url = '../target/WellcomeBot-1.0.0.jar'
  status.textContent = 'Preparing download...'
  fetch(url, { method: 'HEAD' }).then(r => {
    if (r.ok) {
      const a = document.createElement('a')
      a.href = url
      a.download = 'WellcomeBot.jar'
      document.body.appendChild(a)
      a.click()
      a.remove()
      status.textContent = 'Download started'
    } else {
      status.textContent = 'Build not found. Run: mvn -q -DskipTests package'
    }
  }).catch(() => {
    status.textContent = 'Build not found. Run: mvn -q -DskipTests package'
  })
})
