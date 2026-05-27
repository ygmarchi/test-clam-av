### Test manuale

Lanciare

```cmd
docker compose -f docker-compose.yml up -d
docker exec -it clamav sh
```

e dalla shell


```bash
wget -O "/scandir" https://secure.eicar.org/eicar.com
wget  -O "/scandir/eicar.com.txt" https://secure.eicar.org/eicar.com.txt
wget  -O "/scandir/eicar_com.zip" https://secure.eicar.org/eicar_com.zip
wget  -O "/scandir/eicar_com2.zip" https://secure.eicar.org/eicar_com2.zip
```

creare un file `/scandir/scanlist` contente

```txt
/scandir/eicar.com
/scandir/eicar.com.txt
/scandir/eicar_com.zip
/scandir/eicar_com2.zip
```

lanciare

```bash
clamscan -f /scandir/scanlist
```

l'output dovrebbe essere

```txt
Loading:    13s, ETA:   0s [========================>]    3.63M/3.63M sigs       
Compiling:   3s, ETA:   0s [========================>]       41/41 tasks 

/scandir/eicar.com: Eicar-Test-Signature FOUND
/scandir/eicar.com.txt: Eicar-Test-Signature FOUND
/scandir/eicar_com.zip: Eicar-Test-Signature FOUND
/scandir/eicar_com2.zip: Eicar-Test-Signature FOUND

----------- SCAN SUMMARY -----------
Known viruses: 3627866
Engine version: 1.6.0-devel-20260521
Scanned directories: 0
Scanned files: 4
Infected files: 4
Data scanned: 272 B
Data read: 628 B (ratio 0.43:1)
Time: 17.127 sec (0 m 17 s)
Start Date: 2026:05:27 07:45:16
End Date:   2026:05:27 07:45:33
```
### Test client java
La classe `it.bmw.clamav.ClamAVClientTest` contiene un test che esegue le stesse operazioni, ma usando il client Java `io.sensesecure:clamav4j`. 