% classe para as monografias da UFLA
% by Joaquim Quinteiro Uchôa
% 22 de Junho de 2001
% com sugestões de Jones Oliveira de Albuquerque
% e Douglas Machado Tavares
% alterado por Alysson A. Naves Silva (anaves@gmail.com) em 10 de Julho de 2007

% identificacao
\NeedsTeXFormat{LaTeX2e}
\ProvidesClass{uflamon}[2001/06/01]

% código inicial e carregamento de pacotes
\RequirePackage{ifthen} % para testes
%\LoadClass[11pt,a4paper, openany]{book} 
%\LoadClass[11pt,letterpaper]{book} 
\LoadClass[11pt,a4paper, oneside]{book} 
                                % classe base para a monografia
%\RequirePackage[left=2.5cm,right=2.3cm,top=4.0cm,bottom=2.6cm]{geometry} 
\RequirePackage[left=2.9cm,right=2.3cm,top=2.9cm,bottom=2.3cm]{geometry} 
                                % define margens da monografia
                                
%\addtolength{\oddsidemargin}{-0.7cm}
%\addtolength{\evensidemargin}{0.7cm}
\RequirePackage{fancyhdr}
\fancyhead{}
\fancyhead[RO]{\nouppercase{\small\slshape\rightmark}}
\fancyhead[LE]{\nouppercase{\small\slshape\leftmark}}
\fancyfoot{}
\fancyfoot[CO,CE]{\small\thepage}

\fancypagestyle{plain}{
\fancyhf{}
\fancyfoot[C]{\thepage}
\renewcommand{\headrulewidth}{0pt}
\renewcommand{\footrulewidth}{0pt}
}

% abstract e thanks
\renewcommand{\thanks}[1]{
                          %\clearpage
                          \null\vfill
                          \begin{center}\Large{\textbf{Agradecimentos}}\end{center}
                          #1
                          \vspace*{1cm}
                          \clearpage
                         }

\newcommand{\tituloResumo}[1]{
                          %\clearpage
                          %\null\vfill
                          \begin{center}\textbf{#1}\end{center}
                         %\vspace{0.2cm}\vfill\null
                         }
                  
\newcommand{\resumo}[1]{
                          %\clearpage
                          %\null\vfill
                          \begin{center}\Large{\textbf{Resumo}}\end{center}
                          #1
                          \vspace*{0.5cm}%\vfill\null
                         }

\newcommand{\titleAbstract}[1]{
                          %\clearpage
                          %\null\vfill
                          \begin{center}\textbf{#1}\end{center}
                         %\vspace{0.2cm}\vfill\null
                         }
                         
\newcommand{\abstract}[1]{
                          %\clearpage
                          %\null\vfill
                          \begin{center}\Large{\textbf{Abstract}}\end{center}
                          #1
                         % \vspace*{1cm}\vfill\null
                          \clearpage
                         }

\newcommand{\dedic}[1]{
                          %\clearpage
                          \null\vfill
                          \begin{flushright}{\textit{#1}}\end{flushright}
                          \vspace*{1cm}
                          \clearpage
                         }


% código da monografia

\def\tipo#1{\gdef\@tipo{#1}} % tipo da monografia
\def\local#1{\gdef\@local{#1}} % local
\def\orientador#1{\gdef\@orientador{#1}} % orientador
\def\coorientador#1{\gdef\@coorient{#1}} % co-orientador
\def\bancaum#1{\gdef\@bancaum{#1}} % 1. banca
\def\bancadois#1{\gdef\@bancadois{#1}} % 2. banca
\bancadois{} % inicialmente vazia
\def\defesa#1{\gdef\@defesa{#1}} % data da defesa
\def\areaconcentracao#1{\gdef\@areaconcentracao{#1}} % area de concentracao
\def\fichacatalografica#1{\gdef\@fichacatalografica{#1}}
\def\fichacatalograficax#1{\gdef\@fichacatalograficax{#1}}
\def\fichacatalograficay#1{\gdef\@fichacatalograficay{#1}}
\def\fichacatalograficaz#1{\gdef\@fichacatalograficaz{#1}}

% no início, redefinimos o comando de fazer títulos
% e margens

%=============
%pagina 1
%=============
\AtBeginDocument{
\def\maketitle{    %\maketitle
\begin{titlepage}
\pagestyle{empty}
\pagenumbering{roman}
\centering

%\vspace*{\stretch{.58}}

\vspace{2.5cm}

\textbf{\@author} % autor da monografia

\vspace{9cm}

\textbf{\@title} % título da monografia

\vspace{2.5cm}

\hfill\begin{minipage}{10.5cm}{\@tipo}\end{minipage} % tipo da monografia

\vspace{5cm}

\@local\\ 
\@date


% no início, redefinimos o comando de fazer títulos
% e margens

%=============
%pagina 2
%=============
\null

\newpage

\vspace{2.0cm}

\textbf{\@author} % autor da monografia

\vspace{3.5cm}

\textbf{\@title} % título da monografia

\vspace{3.5cm}

\hfill\begin{minipage}{10.5cm}{\@tipo}\end{minipage} % tipo da monografia

\vspace{2cm}
\newlength{\mylen}
\settowidth{\mylen}{\@orientador:}
\hfill\begin{minipage}{9.7cm}
Área de Concentração:\\
\vspace{0.2cm}
\@areaconcentracao 
\end{minipage}

\vspace{0.4cm}

\hfill\begin{minipage}{9.7cm}
Orientador:\\
\vspace{0.2cm}
\@orientador
\end{minipage}

%\ifthenelse{\equal{\@coorient}{\@empty}}{}   % se co-orientador nao existir, nao
                                             % produza nada
%{\vspace{0.4cm}                       % caso contrario, escreva-o
%    \settowidth{\mylen}{\@coorient}
%    \hfill\begin{minipage}{9.4cm}
%      Orientador\\
%      \@orientador      
%    \end{minipage}
   % \hspace{\stretch{.3}}
%}



\vspace{3.5cm} 

\@local\\ 
\@date

%=============
%pagina 3
%=============

\null
\newpage
\small{.}
\vspace{10cm}

\newsavebox{\savepar}
\newenvironment{boxit}{\begin{lrbox}{\savepar}
\begin{minipage}{14cm}}
{\end{minipage}\end{lrbox}\fbox{\usebox{\savepar}}}

\begin{center}
{\bf Ficha Catalográfica preparada pela Divisão de Processos Técnicos \\
da Biblioteca Central da UFLA}

\vspace{0.4cm}
\begin{boxit}
\@fichacatalografica

\vspace{0.5cm}

\hspace{1.0cm} \@fichacatalograficax

\vspace{0.5cm}

\hspace{1.0cm} \@fichacatalograficay

\vspace{0.5cm}

\hspace{1.0cm} \@fichacatalograficaz

\vspace{0.5cm}
\end{boxit}
\end{center}

%=============
%pagina 4
%=============
%\newpage % página do verso em branco

\null

\newpage
%página tres

\textbf{\@author}
%\makeatother

\vspace{2.0cm}

%\makeatletter
\textbf{\@title}
%\makeatother

\vspace{2.0cm}

%\makeatletter
\hfill\begin{minipage}{9.8cm}{\@tipo}\end{minipage} % tipo da monografia
%\makeatother

\vspace{1.5cm}


%{\flushleft
\begin{center}
Aprovada em \textsl{\@defesa} \\
\vspace{1.5cm}
\rule{8.5cm}{1pt}\\
\@bancaum\\
\vspace{0.70cm}
\rule{8.5cm}{1pt}\\
\@bancadois\\
%\vspace{0.70cm}
%\rule{8.5cm}{1pt}\\
%Prof. Dr. Banca\\
\vspace{0.70cm}
\rule{8.5cm}{1pt}\\
\@orientador\\
(Orientador)\\
%\vspace{2cm}                    
%\settowidth{9.5cm}{\@bancadois}
%\hspace{2cm}\rule{9.5cm}{1pt}\\
%\hspace{2cm}\@bancadois\\
\end{center}
%}
\vspace{2cm} 

{\@local}

%\newpage
\null

\end{titlepage}
\pagestyle{plain}
}}
