# Recnicke metode kompresije

Projekat prikazuje rad recnickih metoda kompresije podataka kroz algoritme LZ78 i LZW. U repozitorijumu se nalaze Python i Java implementacija, primeri ulaznih fajlova, rezultati kompresije i seminarski rad.

## Struktura projekta

- `source_code/Python project/` - Python implementacija i gotov `python_impl.exe`.
- `source_code/Java project/` - Java implementacija, kompajlirane klase i Java `java_impl.exe`.
- `data_primeri/` - primeri fajlova za testiranje.
- `compression_results/` - primeri dobijenih rezultata.
- `Seminarski rad.pdf` - prateci seminarski rad.

## Najlakse pokretanje

Najjednostavnije je pokrenuti Python verziju:

```powershell
& ".\source_code\Python project\python_impl.exe"
```

Ovaj fajl otvara graficki interfejs u kome se bira ulazni fajl, algoritam (`lz78` ili `lzw`) i operacija. Za proveru se mogu koristiti fajlovi iz foldera `data_primeri`.

## Pokretanje Python izvornog koda

Ako se pokrece iz izvornog koda, potrebno je uci u folder Python implementacije:

```powershell
cd ".\source_code\Python project\Python implementation"
python dictionary_compression.py compress lz78 -i "..\..\..\data_primeri\kratak_tekst.txt" -o "..\..\..\compression_results\kratak_tekst_lz78_python.rkc"
python dictionary_compression.py decompress lz78 -i "..\..\..\compression_results\kratak_tekst_lz78_python.rkc" -o "..\..\..\compression_results\kratak_tekst_lz78_python_dekompresovan.txt"
```

Za graficki interfejs iz Python koda:

```powershell
python gui_app.py
```

## Pokretanje Java izvornog koda

Za kompajliranje i pokretanje Java konzolne verzije:

```powershell
cd ".\source_code\Java project"
javac -d build (Get-ChildItem -Path ".\Java implementation" -Recurse -Filter *.java).FullName
java -cp build DictionaryCompression lz78 "..\..\data_primeri\tekst_ponavljanja.txt" "..\..\compression_results"
```

Za Java graficki interfejs, ako je Java okruzenje dostupno:

```powershell
java -cp build GuiApp
```

## Napomena za .exe fajlove

Za predaju preko gita najbolja opcija je da ostane dodat `source_code/Python project/python_impl.exe`, jer je to jedan izvrsni fajl koji profesor moze direktno da pokrene na Windows racunaru.

Java `java_impl.exe` bez prateceg runtime foldera nije potpuno samostalna opcija. Moze raditi samo ako su prisutni odgovarajuci `app` fajlovi i kompatibilno Java okruzenje. Dodavanje celog Java runtime foldera na git nije preporucljivo, jer sadrzi mnogo sistemskih fajlova i nepotrebno uvecava repozitorijum.
