package main

import (
	"log"
	"net/http"

	"md-notes-go/internal/health"
)

func main() {
	mux := http.NewServeMux()

	health.RegisteRoute(mux)
	log.Println("Go server listening on :8081")
	if err := http.ListenAndServe(":8081", mux); err != nil {
		log.Fatal(err)
	}
}
