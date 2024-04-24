/*register(username, password)  errore se user gia presente o pass vuota, lato serve info che persistono-->connessione TCP cpn il server
login(username, password)  errore se utente ha gia fatto login o pass errata, restituisce un codice-->dopo la registrazione, conn TCP
logout(username)
searchHotel(nomeHotel,città) anche da utenti non loggati. Invia i dati di un hotel
searchAllHotels(città) anche da utenti non loggati. Hotels ordinati in ordine di ranking.
insertReview(nomeHotel, nomeCittà, GlobalScore,[] SingleScores) l'utente deve essere registrato
showMyBadges() utente registrato con login chiede il proprio distintivo
CODICE BEN COMMENTATO
*/